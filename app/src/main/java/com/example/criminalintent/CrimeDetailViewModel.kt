package com.example.criminalintent

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()

    private val _remoteImageUrl = MutableLiveData<String>()
    val remoteImageUrl = _remoteImageUrl

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }

    fun uploadImage(imageUri: Uri?, crime:Crime){
        crimeRepository.uploadImageToFireBase(imageUri!!){
            _remoteImageUrl.value = it
        }
        saveCrime(crime)
    }

}