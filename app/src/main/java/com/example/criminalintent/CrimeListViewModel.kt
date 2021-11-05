package com.example.criminalintent

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    private val _crimes = MutableLiveData<List<Crime>>()
    val crimeListLiveData: LiveData<List<Crime>> = _crimes
    private val _error = MutableLiveData<String>()

    fun fetchCrimes() {
        crimeRepository.getCrimes { crimes, error ->
            if (error != null) {
                _error.value = error
            } else {
                _crimes.value = crimes
            }
        }
    }

    fun addCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }
    fun deleteCrime(uid: String) {
        crimeRepository.deleteBill(uid)
    }
}
