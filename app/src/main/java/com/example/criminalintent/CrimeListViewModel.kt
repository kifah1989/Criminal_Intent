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
    private val _crimeId = MutableLiveData<String>()
    val crimeId: LiveData<String> = _crimeId

    fun fetchCrimes() {
        crimeRepository.getCrimes { crimes ->
            _crimes.value = crimes
        }
    }

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime){
            _crimeId.value = it
        }
    }
    fun deleteCrime(uid: String) {
        crimeRepository.deleteBill(uid)
    }
}
