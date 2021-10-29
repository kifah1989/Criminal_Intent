package com.example.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

private const val TAG = "MainActivity"

class MainActivity: AppCompatActivity(), CrimeListFragment.Callbacks, LoginFragment.Callbacks {
    private lateinit var logOutBtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        val dataBase = Firebase.firestore


        firestoreListener = dataBase.collection("Crimes")
            .addSnapshotListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@addSnapshotListener
                }
                val crimeList: MutableList<Crime> = ArrayList<Crime>()
                for (doc in documentSnapshots!!) {
                    val note = doc.toObject(Crime::class.java)
                    note.uid = doc.id
                    crimeList.add(note)
                }
            }
        logOutBtn = findViewById(R.id.logOut_btn)
        logOutBtn.setOnClickListener{
            logOutBtn.visibility = View.INVISIBLE
            auth.signOut()
            onLogOut()
        }
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = LoginFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
    override fun onCrimeSelected(crime: Crime) {
        val fragment = CrimeFragment.newInstance(crime)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun newCrime() {
        val fragment = CrimeFragment.newCrime()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onLogin() {
        val fragment = CrimeListFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        logOutBtn.visibility = View.VISIBLE
    }

    override fun onLogOut() {
        val fragment = LoginFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}