package com.example.criminalintent

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import java.io.File
import android.graphics.BitmapFactory

class PhotoViewerFragment : DialogFragment() {
    private var mPhotoView: ImageView? = null
    private var mPhotoFile: File? = null
    @Nullable override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        mPhotoFile = requireArguments().getSerializable(ARG_PHOTO_FILE) as File?
        val view: View = inflater.inflate(R.layout.dialog_photo, container, false)
        mPhotoView = view.findViewById<View>(R.id.photo_view_dialog) as ImageView
        if (mPhotoFile == null || !mPhotoFile!!.exists()) {
            mPhotoView!!.setImageDrawable(null)
        } else {
            setPic(mPhotoFile!!.path, mPhotoView!!)
        }
        return view
    }

    companion object {
        private const val ARG_PHOTO_FILE = "photoFile"
        fun newInstance(photoFile: File?): PhotoViewerFragment {
            val args = Bundle()
            args.putSerializable(ARG_PHOTO_FILE, photoFile)
            val fragment = PhotoViewerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}