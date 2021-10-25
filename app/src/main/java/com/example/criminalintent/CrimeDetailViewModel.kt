package com.example.criminalintent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<Crime>()
    val crimeLiveData: LiveData<Crime> = crimeIdLiveData
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error


    fun loadCrime(crimeId: String) {
        crimeRepository.getCrime(crimeId) { crime, error ->
            if (error != null) {
                _error.value = error
            }
            else {
                crimeIdLiveData.value = crime
            }
        }
    }


    fun saveCrime(crime: Crime) {
            crimeRepository.updateCrime(crime) { _, error ->
                if (error != null) {
                    _error.value = error
                }
                else {
                    Log.d(javaClass.simpleName, crime.toString())
                }
            }
        }
    }