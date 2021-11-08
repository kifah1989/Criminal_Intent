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
import android.net.Uri
import com.bumptech.glide.Glide

class PhotoViewerFragment : DialogFragment() {
    private var mPhotoView: ImageView? = null
    private var mPhotoFile: String? = null
    @Nullable override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        mPhotoFile = requireArguments().getString(ARG_PHOTO_FILE) as String
        val view: View = inflater.inflate(R.layout.dialog_photo, container, false)
        mPhotoView = view.findViewById<View>(R.id.photo_view_dialog) as ImageView
        if (mPhotoFile == null) {
            mPhotoView!!.setImageDrawable(null)
        } else {
            Glide.with(requireContext())
                .load(mPhotoFile)
                .into(mPhotoView!!)
        }
        return view
    }

    companion object {
        private const val ARG_PHOTO_FILE = "photoFile"
        fun newInstance(photoFile: String?): PhotoViewerFragment {
            val args = Bundle()
            args.putString(ARG_PHOTO_FILE, photoFile)
            val fragment = PhotoViewerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}