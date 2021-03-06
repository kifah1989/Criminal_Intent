package com.example.criminalintent

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.Timestamp
import java.io.File
import java.util.*
import android.widget.RelativeLayout

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Picture

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Environment
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import java.io.IOException
import androidx.core.app.NotificationManagerCompat





private const val DATE_FORMAT = "EEE, MMM, dd"
private const val TIME_FORMAT = "hh:mm"
private const val ARG_CRIME_ID = "crime_id"
private const val REQUEST_DATE = "DialogDate"
private const val REQUEST_TIME = "DialogTime"
private const val REQUEST_BARCODE = "FragmentBarCode"
private const val TAG = "CrimeFragment"
private const val DIALOG_PHOTO = "DialogPhoto"
private const val REQUEST_IMAGE_CAPTURE = 1

class CrimeFragment : Fragment(), FragmentResultListener {
    private lateinit var photoUri: Uri
    private lateinit var data: Intent
    private lateinit var suspectNameResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var barcodeButton: Button
    private lateinit var generateBarCodeBtn: Button
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var policeRequired: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var callSuspect: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView
    private var callbacks: Callbacks? = null


    interface Callbacks {
        fun newBarCode(requestCode: String) {
        }
        fun crimeReport(crimeReport: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        if(arguments !=null) {
            crime.uid = arguments?.getString(ARG_CRIME_ID) as String
            crime.title = arguments?.getSerializable("CrimeTitle") as String
            crime.date = Timestamp(arguments?.getSerializable("CrimeDate") as Date)
            crime.time = Timestamp(arguments?.getSerializable("CrimeTime") as Date)
            crime.isSolved = arguments?.getSerializable("CrimeSolved") as Boolean
            crime.requiresPolice = arguments?.getSerializable("CrimeRequirePolice") as Boolean
            crime.suspect = arguments?.getSerializable("CrimeSuspect") as String
            crime.suspectPhoneNumber = arguments?.getSerializable("sphone") as String
            crime.photoRemoteUrl = arguments?.getSerializable("photoUrl") as String
        }

        suspectNameResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when {
                    result.resultCode != Activity.RESULT_OK -> return@registerForActivityResult

                    result.data != null -> {
                        data = result.data!!
                        val contactUri: Uri? = data.data
                        // queryFieldsName: a List to return the DISPLAY_NAME Column Only
                        val queryFieldsName = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

                        // queryFieldsId: a List to return the _ID Column Only, i will use it to get the suspect Id
                        val queryFieldsId = arrayOf(ContactsContract.Contacts._ID)

                        val cursorName = requireActivity().contentResolver
                            .query(contactUri!!, queryFieldsName, null, null, null)
                        cursorName?.use {
                            if (it.count == 0) {
                                return@registerForActivityResult
                            }

                            it.moveToFirst()
                            val suspect = it.getString(0)
                            crime.suspect = suspect
                            suspectButton.text = suspect
                        }

                        // I created another Cursor to get the suspect Id
                        val cursorId = requireActivity().contentResolver
                            .query(contactUri, queryFieldsId, null, null, null)
                        cursorId?.use {
                            if (it.count == 0) {
                                return@registerForActivityResult
                            }

                            it.moveToFirst()
                            // here i put the suspect Id in contactId to use it later (to get the phone number)
                            val contactId = it.getString(0)

                            // This is the Uri to get a Phone number
                            val phoneURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

                            // phoneNumberQueryFields: a List to return the PhoneNumber Column Only

                            val phoneNumberQueryFields =
                                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

                            // phoneWhereClause: A filter declaring which rows to return, formatted as an SQL WHERE clause                                  (excluding the WHERE itself)
                            val phoneWhereClause =
                                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"

                            // This val replace the question mark in the phoneWhereClause  val
                            val phoneQueryParameters = arrayOf(contactId)

                            val phoneCursor = requireActivity().contentResolver
                                .query(
                                    phoneURI,
                                    phoneNumberQueryFields,
                                    phoneWhereClause,
                                    phoneQueryParameters,
                                    null
                                )

                            phoneCursor?.use { cursorPhone ->
                                cursorPhone.moveToFirst()
                                val phoneNumValue = cursorPhone.getString(0)

                                // after retrieving the phone number i put it in the crime.phone
                                crime.suspectPhoneNumber = phoneNumValue
                                updateUI()
                            }
                        }
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        barcodeButton = view.findViewById(R.id.barcodeButton) as Button
        generateBarCodeBtn = view.findViewById(R.id.generateBtn) as Button
        dateButton = view.findViewById(R.id.crime_date) as Button
        timeButton = view.findViewById(R.id.crime_time) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved_fr) as CheckBox
        policeRequired = view.findViewById(R.id.require_polic) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        callSuspect = view.findViewById(R.id.crime_call_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView

        return view
    }

    private val observeRemoteImage = Observer<String>{
        crime.photoRemoteUrl = it
        updatePhotView()
            Log.d("crimeFragment", it.toString())
    }

    private fun updatePhotView() {
        Glide.with(requireContext())
            .load(crime.photoRemoteUrl)
            .into(photoView)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.remoteImageUrl.observe(viewLifecycleOwner, observeRemoteImage)
        updateUI()
        updatePhotView()
        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
        childFragmentManager.setFragmentResultListener(REQUEST_TIME, viewLifecycleOwner, this)
        parentFragmentManager.setFragmentResultListener(REQUEST_BARCODE, viewLifecycleOwner, this)

    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
// This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
// This one too
            }
        }
        titleField.addTextChangedListener(titleWatcher)
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
        policeRequired.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.requiresPolice = isChecked
            }
        }
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(Timestamp(Date()), REQUEST_DATE)
                .show(childFragmentManager, REQUEST_DATE)
        }
        timeButton.setOnClickListener {
            TimePickerFragment.newInstance(Timestamp(Date()), REQUEST_TIME)
                .show(childFragmentManager, REQUEST_TIME)
        }
        barcodeButton.setOnClickListener{
            callbacks?.newBarCode(REQUEST_BARCODE)
        }
        generateBarCodeBtn.setOnClickListener{
            val fragment = GenerateBarCodeFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        reportButton.setOnClickListener {
            crimeDetailViewModel.saveCrime(crime)
            //parentFragmentManager.popBackStack()
            callbacks?.crimeReport(
                getCrimeReport())

//            Intent(Intent.ACTION_SEND).apply {
//                type = "text/plain"
//                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
//                putExtra(
//                    Intent.EXTRA_SUBJECT,
//                    getString(R.string.crime_report_subject)
//                )
//            }.also { intent ->
//                startActivity(intent)
//            }
        }
        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                suspectNameResultLauncher.launch(pickContactIntent)
            }
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }
        callSuspect.apply {
            setOnClickListener {
                val callContactIntent =
                    Intent(Intent.ACTION_DIAL).apply {

                        val phone = crime.suspectPhoneNumber
                        data = Uri.parse("tel:$phone")
                    }
                startActivity(callContactIntent)
            }
        }
        photoButton.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                val packageManager: PackageManager = requireActivity().packageManager
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File

                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        photoUri = FileProvider.getUriForFile(
                            requireActivity(),
                            "com.example.criminalintent.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }
        photoView.setOnClickListener {
            val manager = parentFragmentManager
            val dialog: PhotoViewerFragment = PhotoViewerFragment.newInstance(crime.photoRemoteUrl)
            dialog.show(manager, DIALOG_PHOTO)
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val filesDir = context?.applicationContext?.filesDir

        // Create an image file name
            return File(filesDir,"JPEG_${crime.uid}.jpg")
                // Save a file: path for use with ACTION_VIEW intents

        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            crimeDetailViewModel.uploadImage(photoUri, crime)
        }
    }

    override fun onFragmentResult(requestCode: String, result: Bundle) {
        when (requestCode) {
            REQUEST_TIME -> {
                Log.d(TAG, "received result for $requestCode")
                crime.time = TimePickerFragment.getSelectedTime(result)
                updateUI()
            }
            REQUEST_DATE -> {
                Log.d(TAG, "received result for $requestCode")
                crime.date = DatePickerFragment.getSelectedDate(result)
                updateUI()
            }
            REQUEST_BARCODE ->{
                Log.d(TAG, "received result for $requestCode")
                crime.barcode = CodeScannerFragment.getBarCode(result) as String
            }
        }
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = DateFormat.format("EEE dd MMM yyyy", crime.date.toDate())
        timeButton.text = DateFormat.format("hh:mm", crime.time.toDate())
        solvedCheckBox.isChecked = crime.isSolved
        policeRequired.isChecked = crime.requiresPolice
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
            callSuspect.text = getString(R.string.crime_call_suspect_text, crime.suspectPhoneNumber)
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT, crime.date.toDate()).toString()
        val timeString = DateFormat.format(TIME_FORMAT, crime.time.toDate()).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(
            R.string.crime_report,
            crime.title, dateString, timeString, solvedString, suspect
        )
    }

    companion object {
        fun newInstance(crime: Crime): CrimeFragment {
            val args = Bundle().apply {
                putString(ARG_CRIME_ID, crime.uid)
                putSerializable("CrimeTitle", crime.title)
                putSerializable("CrimeDate", crime.date.toDate())
                putSerializable("CrimeTime", crime.time.toDate())
                putSerializable("CrimeSolved", crime.isSolved)
                putSerializable("CrimeRequirePolice", crime.requiresPolice)
                putSerializable("CrimeSuspect", crime.suspect)
                putSerializable("sphone", crime.suspectPhoneNumber)
                putSerializable("photoUrl", crime.photoRemoteUrl)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}
