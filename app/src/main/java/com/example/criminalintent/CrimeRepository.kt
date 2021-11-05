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

private const val CRIME_COLLECTION = "Crimes"


class CrimeRepository private constructor(context: Context) {

    private val dataBase = Firebase.firestore
    private val filesDir = context.applicationContext.filesDir

    fun getPhotoFile(photoName:String): File = File(filesDir, photoName)

    fun uploadImageToFireBase(imageUri: Uri, callback: (String?) -> Unit){
        val fileName = File(imageUri.path!!).name
val storageRef = FirebaseStorage.getInstance().getReference("images/$fileName")
        storageRef.putFile(imageUri).addOnSuccessListener {
            val downloadUrl = storageRef.downloadUrl
            downloadUrl.addOnSuccessListener {

                val remoteUri = it.toString()
                callback(remoteUri)
                // update our Cloud Firestore with the public image URI.
            }
        }
    }

    fun getCrimes(callback: (List<Crime>?, String?) -> Unit) {
        dataBase.collection(CRIME_COLLECTION)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val crimeList: MutableList<Crime> = ArrayList<Crime>()
                    for (doc in it.result!!) {
                        val crime: Crime = doc.toObject(Crime::class.java)
                        crime.uid = doc.id
                        crimeList.add(crime)
                    }
                    callback(crimeList, null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null, exception.message)
            }
    }

    fun addCrime(crime: Crime) {
        dataBase.collection(CRIME_COLLECTION)
            .add(crime)
            .addOnSuccessListener {
                it.id
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
        dataBase.collection(CRIME_COLLECTION)
            .document(uid.toString())
            .delete()
            .addOnSuccessListener {
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
