package com.example.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.firebase.Timestamp
import java.util.*
private const val ARG_TIME = "Date"
private const val ARG_REQUEST_CODE_TIME = "requestCode"
private const val RESULT_TIME_KEY = "DialogTime"

class TimePickerFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener = TimePickerDialog.OnTimeSetListener { _:TimePicker, hour: Int, minute: Int ->
            val resultTime = Calendar.getInstance()
            resultTime.set (resultTime.get(Calendar.YEAR), Calendar.MONTH, 1, hour, minute, Calendar.SECOND)
                    //Date = GregorianCalendar(0,0,0,hour,minute,0).time
            val result = Bundle().apply {
                putSerializable(RESULT_TIME_KEY, resultTime.time)
            }
            val resultRequestCode = requireArguments().getString(ARG_REQUEST_CODE_TIME, "")
            setFragmentResult(resultRequestCode, result)
        }


        val time = arguments?.getSerializable(ARG_TIME) as Date
        val clock = Calendar.getInstance()
        clock.time = time
        val initialHour = clock.get(Calendar.HOUR)
        val initialMinute = clock.get(Calendar.MINUTE)
        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            false

        )
    }
    companion object {
        fun newInstance(time: Timestamp, requestCode: String): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, time.toDate())
                putString(ARG_REQUEST_CODE_TIME, requestCode)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
        fun getSelectedTime(result: Bundle) = Timestamp(result.getSerializable(RESULT_TIME_KEY) as Date)

    }
}