package com.example.criminalintent

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    private val _imageUri = MutableLiveData<Uri>()
    val image : LiveData<Uri> = _imageUri

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }
    fun getPhotoFile(photoName: String): File {
        return crimeRepository.getPhotoFile(photoName)
    }
    fun getPhotoFileFromFirebase(photoName: String){
        crimeRepository.getPhotoFromFireBase(photoName){
            _imageUri.value = it
        }

    }
    fun uploadImage(imageUri: Uri){
        crimeRepository.uploadImageToFireBase(imageUri)
    }

}