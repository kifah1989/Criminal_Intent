package com.example.criminalintent

import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.util.*

private const val CRIME_COLLECTION = "Crimes"


class CrimeRepository private constructor(context: Context) {

    private val dataBase = Firebase.firestore
    private val filesDir = context.applicationContext.filesDir

    fun getPhotoFile(photoName:String): File = File(filesDir, photoName)

    fun getCrimes(callback: (List<Crime>?, String?) -> Unit) {
        dataBase.collection(CRIME_COLLECTION)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val crimeList: MutableList<Crime> = ArrayList<Crime>()
                    for (doc in task.result!!) {
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
