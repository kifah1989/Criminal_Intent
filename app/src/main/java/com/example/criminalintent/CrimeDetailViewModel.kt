package com.example.criminalintent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()


    fun saveCrime(crime: Crime) {
            crimeRepository.updateCrime(crime)
        }
    fun addCrime(crime: Crime){
        crimeRepository.addCrime(crime)
    }
    }