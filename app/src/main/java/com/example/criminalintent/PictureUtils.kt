package com.example.criminalintent

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Point
import android.os.Build
import android.view.View
import android.widget.ImageView
import kotlin.math.roundToInt


fun setPic(path:String, imageView: ImageView) {
    // Get the dimensions of the View
    val targetW: Int = imageView.maxWidth
    val targetH: Int = imageView.maxHeight

    val bmOptions = BitmapFactory.Options().apply {
        // Get the dimensions of the bitmap
        inJustDecodeBounds = true

        BitmapFactory.decodeFile(path, this)

        val photoW: Int = outWidth
        val photoH: Int = outHeight

        // Determine how much to scale down the image
        val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

        // Decode the image file into a Bitmap sized to fill the View
        inJustDecodeBounds = false
        inSampleSize = scaleFactor
        inPurgeable = true
    }
    BitmapFactory.decodeFile(path, bmOptions)?.also { bitmap ->
        imageView.setImageBitmap(bitmap)
    }
}

