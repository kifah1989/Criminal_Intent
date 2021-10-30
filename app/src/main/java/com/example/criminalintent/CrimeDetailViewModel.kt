package com.example.criminalintent

import androidx.lifecycle.ViewModel

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }
}