package com.example.criminalintent

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import java.util.*

private const val DIALOG_DATE = "Date"
private const val DIALOG_TIME = "Time"
private const val ARG_CRIME_ID = "crime_id"
private const val REQUEST_DATE = "DialogDate"
private const val REQUEST_TIME = "DialogTime"
private const val TAG = "CrimeFragment"
class CrimeFragment : Fragment(), FragmentResultListener {
    private var crimeRequirePolice: Boolean? = null
    private  var crimeSolved: Boolean? = null
    private var crimeTime: Date? = Date()
    private  var crimeDate: Date? = Date()
    private var crimeTitle: String? = ""
    private lateinit var viewModel: CrimeDetailViewModel
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var policeRequired: CheckBox
    private var crimeId:String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
        crimeDate = Date()
        crimeTime = Date()
        crimeTitle = ""
        crimeSolved = false
        crimeRequirePolice = false
        if(arguments!=null){
            crimeId = arguments?.getSerializable(ARG_CRIME_ID) as? String
            crimeTitle = arguments?.getSerializable("CrimeTitle") as? String
            crimeDate = arguments?.getSerializable("CrimeDate") as? Date
            crimeTime = arguments?.getSerializable("CrimeTime") as? Date
            crimeSolved = arguments?.getSerializable("CrimeSolved") as? Boolean
            crimeRequirePolice = arguments?.getSerializable("CrimeRequirePolice") as? Boolean
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
    override fun onStop() {
        super.onStop()
        if (crimeTitle!!.isNotEmpty()) {
            if (crimeId!!.isNotEmpty()) {
                val crime = Crime(crimeId, crimeTitle, Timestamp(crimeDate!!), Timestamp(crimeTime!!), crimeSolved!!, crimeRequirePolice)
                viewModel.saveCrime(crime)
            } else {
                val crime = Crime(crimeTitle, Timestamp(crimeDate!!), Timestamp(crimeTime!!), crimeSolved!!, crimeRequirePolice)
                viewModel.addCrime(crime)
            }
        }
    }

    private fun updateUI() {
        titleField.setText(crimeTitle!!)
        dateButton.text = DateFormat.format("EEE dd MMM yyyy", crimeDate!!)
        timeButton.text = DateFormat.format("hh:mm", crimeTime!!)
        solvedCheckBox.isChecked = crimeSolved!!
        policeRequired.isChecked = crimeRequirePolice!!

    }
    companion object {
        fun newInstance(crime: Crime): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crime.uid)
                putSerializable("CrimeTitle", crime.title)
                putSerializable("CrimeDate", crime.date?.toDate())
                putSerializable("CrimeTime", crime.time?.toDate())
                putSerializable("CrimeSolved", crime.isSolved)
                putSerializable("CrimeRequirePolice", crime.requiresPolice)

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
