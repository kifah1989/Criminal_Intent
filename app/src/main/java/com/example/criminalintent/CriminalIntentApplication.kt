package com.example.criminalintent

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics

private lateinit var firebaseAnalytics: FirebaseAnalytics

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        CrimeRepository.initialize(this)
    }
}
