package com.example.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.sql.Time
import java.util.*
private const val ARG_TIME = "Time"
private const val ARG_REQUEST_CODE_TIME = "requestCodeTime"
private const val RESULT_TIME_KEY = "DialogTime"

class TimePickerFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener =
            TimePickerDialog.OnTimeSetListener { _:TimePicker, hour: Int, minute: Int ->
            val resultTime: Date = GregorianCalendar(0,0,0,hour,minute,0).time
            val result = Bundle().apply {
                putSerializable(RESULT_TIME_KEY, resultTime)
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
            true

        )
    }
    companion object {
        fun newInstance(time: Date, requestCode: String): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, time)
                putString(ARG_REQUEST_CODE_TIME, requestCode)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
        fun getSelectedTime(result: Bundle) = result.getSerializable(RESULT_TIME_KEY) as Date

    }

}