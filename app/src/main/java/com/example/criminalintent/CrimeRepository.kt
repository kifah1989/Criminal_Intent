package com.example.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.Executors

private const val CRIME_COLLECTION = "Crimes"

class CrimeRepository private constructor(context: Context) {

    private val dataBase = Firebase.firestore

    fun getCrimes(callback: (List<Crime>?, String?) -> Unit) {
            dataBase.collection(CRIME_COLLECTION)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val crimeList: MutableList<Crime> = ArrayList<Crime>()
                        for (doc in task.result!!){
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
            }
            .addOnFailureListener {
            }
    }

    fun updateCrime(crime: Crime) {
        if(crime.uid != null){
            dataBase.collection(CRIME_COLLECTION)
                .document(crime.uid!!).set(crime)
                .addOnSuccessListener {
                }
                .addOnFailureListener {
                }
        }

    }

    fun deleteBill(uid: String) {
        dataBase.collection(CRIME_COLLECTION)
            .document(uid)
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
