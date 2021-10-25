package com.example.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.concurrent.Executors

private const val CRIME_COLLECTION = "Crimes"

class CrimeRepository private constructor(context: Context) {

    private val dataBase = FirebaseFirestore.getInstance()
    fun getCrimes(callback: (List<Crime>?, String?) -> Unit) {
            dataBase.collection(CRIME_COLLECTION)
                .get()
                .addOnSuccessListener { result ->

                    val listOf = arrayListOf<Crime>()
                    result.forEach {
                        val crime = Crime.fromData(it)
                        listOf.add(crime)
                    }
                    callback(listOf, null)
                }
                .addOnFailureListener { exception ->
                    callback(null, exception.message)
                }


    }

    fun getCrime(id: String, callback: (Crime?, String?)-> Unit) {
            dataBase.collection(CRIME_COLLECTION)
            .document(id)
                .get()
                .addOnSuccessListener { document ->
                val crime = Crime.fromDocument(document)
                callback(crime, null)
            }
            .addOnFailureListener { exception ->
                callback(null, exception.message)
            }

    }

    fun addCrime(crime: Crime, callback: (Crime?, String?) -> Unit) {
        dataBase.collection(CRIME_COLLECTION)
            .add(crime)
            .addOnSuccessListener {
                callback(crime.apply {
                    uid = it.id }, null)
            }
            .addOnFailureListener { exception ->
                callback(null, exception.message)
            }
    }

    fun updateCrime(crime: Crime, callback: (Crime?, String?) -> Unit) {
        dataBase.collection(CRIME_COLLECTION)
            .document(crime.uid!!).set(crime)

            .addOnSuccessListener {
                callback(crime, null)
            }
            .addOnFailureListener { exception ->
                callback(null, exception.message)
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
