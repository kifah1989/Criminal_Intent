package com.example.criminalintent

import android.R.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

import android.R.attr.src
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import java.io.FileOutputStream
import java.io.IOException
import android.provider.MediaStore

import android.os.Environment
import android.util.Log
import android.widget.*
import java.io.File
import java.io.OutputStream
import java.lang.Exception


class GenerateBarCodeFragment: Fragment() {
    private lateinit var serialText: EditText
    private lateinit var generateButton: Button
    private lateinit var barcodeImg: ImageView
    private lateinit var printImgBtn: ImageButton
    private lateinit var newBitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.generate_barcode_layout, container, false)
        serialText = view.findViewById(R.id.serialTxt) as EditText
        generateButton = view.findViewById(R.id.generateBtn) as Button
        barcodeImg = view.findViewById(R.id.barcodeImg) as ImageView
        printImgBtn = view.findViewById(R.id.printBtn) as ImageButton
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateButton.setOnClickListener{
            getQrCodeBitmap(serialText.text.toString())
        }
        printImgBtn.setOnClickListener{
            saveMediaToStorage(newBitmap)
        }
    }
    fun getQrCodeBitmap(serial:String) {
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val size = 600 //pixels
        val bits = QRCodeWriter().encode(
            serial,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        )
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
        addStampToImage(bitmap)
    }

    private fun addStampToImage(originalBitmap: Bitmap) {
        val extraHeight = (originalBitmap.height * 0.15).toInt()
        newBitmap = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height + extraHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.BLACK)
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)
        val resources = resources
        val scale = resources.displayMetrics.density
        val text = serialText.text.toString()
        val pText = Paint()
        pText.color = Color.WHITE
        setTextSizeForWidth(pText, (originalBitmap.height * 0.10).toFloat(), text)
        val bounds = Rect()
        pText.getTextBounds(text, 0, text.length, bounds)
        val textHeightWidth = Rect()
        pText.getTextBounds(text, 0, text.length, textHeightWidth)
        canvas.drawText(
            text, (canvas.width / 2 - textHeightWidth.width() / 2).toFloat(),
            (originalBitmap.height + extraHeight / 2 + textHeightWidth.height() / 2).toFloat(),
            pText
        )
        barcodeImg.setImageBitmap(newBitmap)
    }


    private fun setTextSizeForWidth(
        paint: Paint, desiredHeight: Float,
        text: String
    ) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        val testTextSize = 2f

        // Get the bounds of the text, using our testTextSize.
        paint.textSize = testTextSize
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        // Calculate the desired size as a proportion of our testTextSize.
        val desiredTextSize = testTextSize * desiredHeight / bounds.height()

        // Set the paint for that size.
        paint.textSize = desiredTextSize
    }
    fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "${serialText.text}.png"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context?.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(requireContext(),"Saved to Photos",Toast.LENGTH_SHORT)
        }
    }
}