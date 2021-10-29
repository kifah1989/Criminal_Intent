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


    fun saveCrime(crime: Crime) {
            crimeRepository.updateCrime(crime)
        }
    fun addCrime(crime: Crime){
        crimeRepository.addCrime(crime)
    }
    }