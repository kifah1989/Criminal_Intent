package com.example.criminalintent

import androidx.lifecycle.ViewModel
import kotlin.random.Random.Default.nextBoolean

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()
    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }


}
