package com.example.criminalintent

import androidx.lifecycle.ViewModel
import kotlin.random.Random.Default.nextBoolean

class CrimeListViewModel : ViewModel() {
    val crimes = mutableListOf<Crime>()
    init {
        for (i in 0 until 100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = nextBoolean()
            crime.requiresPolice = nextBoolean()
            crimes += crime
        }
    }
}
