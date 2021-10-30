package com.example.criminalintent

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import java.util.*

private const val DATE_FORMAT = "EEE, MMM, dd"
private const val TIME_FORMAT = "hh:mm"
private const val ARG_CRIME_ID = "crime_id"
private const val REQUEST_DATE = "DialogDate"
private const val REQUEST_TIME = "DialogTime"
private const val TAG = "CrimeFragment"
class CrimeFragment : Fragment(), FragmentResultListener {
    private lateinit var crimeId: String
    private lateinit var crimeTitle: String
    private  lateinit var crimeDate: Date
    private lateinit var crimeTime: Date
    private var crimeSolved: Boolean = false
    private var crimeRequirePolice: Boolean = false
    private lateinit var crimeSuspect: String
    private lateinit var viewModel: CrimeDetailViewModel
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var policeRequired: CheckBox
    private lateinit var reportButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
        crimeId = ""
        crimeDate = Date()
        crimeTime = Date()
        crimeTitle = ""
        crimeSolved = false
        crimeRequirePolice = false
        crimeSuspect = ""
        if(arguments!=null){
            crimeId = arguments?.getSerializable(ARG_CRIME_ID) as String
            crimeTitle = arguments?.getSerializable("CrimeTitle") as String
            crimeDate = arguments?.getSerializable("CrimeDate") as Date
            crimeTime = arguments?.getSerializable("CrimeTime") as Date
            crimeSolved = arguments?.getSerializable("CrimeSolved") as Boolean
            crimeRequirePolice = arguments?.getSerializable("CrimeRequirePolice") as Boolean
            crimeSuspect = arguments?.getSerializable("CrimeSuspect") as String
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        timeButton = view.findViewById(R.id.crime_time) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved_fr) as CheckBox
        policeRequired = view.findViewById(R.id.require_polic) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
        childFragmentManager.setFragmentResultListener(REQUEST_TIME, viewLifecycleOwner, this)
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
                crimeTitle = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) {
// This one too
            }
        }
        titleField.addTextChangedListener(titleWatcher)
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crimeSolved = isChecked
            }
        }
        policeRequired.apply {
            setOnCheckedChangeListener { _,isChecked ->
                crimeRequirePolice = isChecked
            }
        }
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(Timestamp(Date()), REQUEST_DATE)
                .show(childFragmentManager, REQUEST_DATE)
        }
        timeButton.setOnClickListener{
            TimePickerFragment.newInstance(Timestamp(Date()), REQUEST_TIME)
                .show(childFragmentManager, REQUEST_TIME)
        }
        reportButton.setOnClickListener {
            if (crimeTitle.isNotEmpty()) {
                if (crimeId.isNotEmpty()) {
                    val crime = Crime(crimeId, crimeTitle, Timestamp(crimeDate), Timestamp(crimeTime), crimeSolved, crimeRequirePolice, crimeSuspect)
                    viewModel.saveCrime(crime)
                } else {
                    val crime = Crime(crimeTitle, Timestamp(crimeDate), Timestamp(crimeTime), crimeSolved, crimeRequirePolice, crimeSuspect)
                    viewModel.addCrime(crime)
                }
            }
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject))
            }.also { intent ->
                startActivity(intent)
                parentFragmentManager.popBackStack()
            }
        }
    }
    override fun onFragmentResult(requestCode: String, result: Bundle) {
        when(requestCode) {
            REQUEST_TIME -> {
                Log.d(TAG, "received result for $requestCode")
                crimeTime = TimePickerFragment.getSelectedTime(result).toDate()
                updateUI()
            }
            REQUEST_DATE -> {
                Log.d(TAG, "received result for $requestCode")
                crimeDate = DatePickerFragment.getSelectedDate(result)
                updateUI()
            }
        }
    }
    private fun updateUI() {
        titleField.setText(crimeTitle)
        dateButton.text = DateFormat.format("EEE dd MMM yyyy", crimeDate)
        timeButton.text = DateFormat.format("hh:mm", crimeTime)
        solvedCheckBox.isChecked = crimeSolved
        policeRequired.isChecked = crimeRequirePolice
    }
    private fun getCrimeReport(): String {
        val solvedString = if (crimeSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT, crimeDate).toString()
        val timeString = DateFormat.format(TIME_FORMAT, crimeTime).toString()
        val suspect = if (crimeSuspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crimeSuspect)
        }
        return getString(R.string.crime_report,
            crimeTitle, dateString, timeString, solvedString, suspect)
    }
    companion object {
        fun newInstance(crime: Crime): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crime.uid)
                putSerializable("CrimeTitle", crime.title)
                putSerializable("CrimeDate", crime.date.toDate())
                putSerializable("CrimeTime", crime.time.toDate())
                putSerializable("CrimeSolved", crime.isSolved)
                putSerializable("CrimeRequirePolice", crime.requiresPolice)
                putSerializable("CrimeSuspect", crime.suspect)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
        fun newCrime(): CrimeFragment{
            return CrimeFragment()
        }
    }
}
