package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class CrimeReport: Fragment() {
    private lateinit var textReport: TextView
    private lateinit var reportText:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportText = arguments?.getString("crimeReport") as String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.report_fragment, container, false)
        textReport = view.findViewById(R.id.textReport)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textReport.text = reportText
    }

    companion object {
        fun newReport(crimeReport: String): CrimeReport {
            val args = Bundle().apply {
                putString("crimeReport", crimeReport)
                }
            return CrimeReport().apply {
                arguments = args
            }
        }
    }
}