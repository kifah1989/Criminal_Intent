package com.example.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    private val _crimes = MutableLiveData<List<Crime>>()
    val crimeListLiveData: LiveData<List<Crime>> = _crimes


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchCrimes() {
        crimeRepository.getCrimes{ crimes, error ->
            if (error != null) {
                _error.value = error
            }
            else {
                _crimes.value = crimes
            }
        }
    }
    fun addCrime(crime: Crime?) {
                crimeRepository.addCrime(crime!!)
    }
    fun deleteCrime(uid: String){
        crimeRepository.deleteBill(uid)
    }
}
