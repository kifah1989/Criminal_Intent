package com.example.criminalintent

import android.content.ClipData
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import android.widget.Toast

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.storage.UploadTask

import com.google.android.gms.tasks.OnSuccessListener

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.provider.MediaStore
import android.view.View
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.firestore.ListenerRegistration
import java.io.ByteArrayOutputStream





private const val CRIME_COLLECTION = "Crimes"
private const val TAG = "CrimeReository"

class CrimeRepository private constructor(context: Context) {

    private val dataBase = Firebase.firestore
    private lateinit var fireStoreListener: ListenerRegistration
    private val filesDir = context.applicationContext.filesDir

    fun getPhotoFile(photoName:String): File = File(filesDir, photoName)

    fun uploadImageToFireBase(imageUri: Uri, callback: (String?) -> Unit){
        val fileName = File(imageUri.path!!).name
        val storageRef = FirebaseStorage.getInstance().getReference("images/$fileName")
        val bmp = BitmapFactory.decodeFile(getPhotoFile(fileName).absolutePath)
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        val data = baos.toByteArray()
        //uploading the image
        //uploading the image
        val uploadTask2: UploadTask = storageRef.putBytes(data)
        uploadTask2.addOnSuccessListener {
            val downloadUrl = storageRef.downloadUrl
            downloadUrl.addOnSuccessListener {
                val remoteUri = it.toString()
                callback(remoteUri)
            }
        }.addOnFailureListener {

        }
    }

    fun getCrimes(callback: (List<Crime>?) -> Unit) {
        fireStoreListener = dataBase.collection("Crimes")
            .addSnapshotListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@addSnapshotListener
                }
                val crimeList: MutableList<Crime> = java.util.ArrayList<Crime>()
                for (doc in documentSnapshots!!) {
                    val crime = doc.toObject(Crime::class.java)
                    crime.uid = doc.id
                    crimeList.add(crime)
                }
                callback(crimeList)

            }
    }

    fun addCrime(crime: Crime, callback: (String) -> Unit) {
        dataBase.collection(CRIME_COLLECTION)
            .add(crime)
            .addOnSuccessListener {
                callback(it.id)
            }
            .addOnFailureListener {
            }
    }

    fun updateCrime(crime: Crime) {
        dataBase.collection(CRIME_COLLECTION)
            .document(crime.uid).set(crime)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }

    fun deleteBill(uid: String) {
        val storageRef = FirebaseStorage.getInstance().getReference("images/JPEG_${uid}.jpg")
        dataBase.collection(CRIME_COLLECTION)
            .document(uid)
            .delete()
            .addOnSuccessListener {
                storageRef.delete()
            }
            .addOnFailureListener {
            }
    }

    companion object {

        private var INSTANCE: CrimeRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}
