package com.example.criminalintent

import androidx.lifecycle.ViewModel
import java.io.File

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }
    fun getPhotoFile(photoName: String): File {
        return crimeRepository.getPhotoFile(photoName)
    }

}