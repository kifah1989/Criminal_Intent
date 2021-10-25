package com.example.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var viewModel: CrimeDetailViewModel
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var policeRequired: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: String = arguments?.getSerializable(ARG_CRIME_ID) as String
        viewModel = ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
        viewModel.loadCrime(crimeId)
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
    private val observerCrime = Observer<Crime> { crime ->
            this.crime = crime
            updateUI()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crimeLiveData.observe(viewLifecycleOwner, observerCrime)
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
            setOnCheckedChangeListener { _,isChecked ->
                crime.requiresPolice = isChecked
            }
        }
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date!!, REQUEST_DATE)
                .show(childFragmentManager, REQUEST_DATE)

        }
        timeButton.setOnClickListener{
            TimePickerFragment.newInstance(crime.time!!, REQUEST_TIME)
                .show(childFragmentManager, REQUEST_TIME)
        }
    }
    override fun onFragmentResult(requestCode: String, result: Bundle) {
        when(requestCode) {
            REQUEST_TIME -> {
                Log.d(TAG, "received result for $requestCode")
                crime.time = Timestamp(TimePickerFragment.getSelectedTime(result))
            }
            REQUEST_DATE -> {
                Log.d(TAG, "received result for $requestCode")
                crime.date = Timestamp(DatePickerFragment.getSelectedDate(result))
            }
        }
    }
    override fun onStop() {
        super.onStop()
        viewModel.saveCrime(crime)
    }

    private fun updateUI() {
        Log.d(TAG, crime.toString())
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        timeButton.text = crime.time.toString()
        solvedCheckBox.isChecked = crime.isSolved!!
        policeRequired.isChecked = crime.requiresPolice!!

    }
    companion object {
        fun newInstance(crimeId: String): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
        fun newCrime(): CrimeFragment {
            return CrimeFragment()
        }
    }
}
