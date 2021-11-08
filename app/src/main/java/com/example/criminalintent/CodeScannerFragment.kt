package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.google.firebase.Timestamp
import java.util.*
private const val ARG_REQUEST_CODE = "barcodeRequest"
private const val RESULT_BARCODE_KEY = "FragmentBarCode"

class CodeScannerFragment: Fragment() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scannner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                val result = Bundle().apply {
                    putString(RESULT_BARCODE_KEY, it.text)
                }
                val resultRequestCode = requireArguments().getString(ARG_REQUEST_CODE, "")
                setFragmentResult(resultRequestCode, result)
                Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
    companion object {
        fun newInstance(requestCode:String): CodeScannerFragment {
            val args = Bundle().apply {
                putString(ARG_REQUEST_CODE, requestCode)
            }
            return CodeScannerFragment().apply {
                arguments = args
            }
        }
        fun getBarCode(result: Bundle) = result.getString(RESULT_BARCODE_KEY)
    }
}
