package com.example.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "MainActivity"

class MainActivity: AppCompatActivity(), CrimeListFragment.Callbacks, LoginFragment.Callbacks {
    private lateinit var logOutBtn: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
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
    override fun onCrimeSelected(crimeId: String) {
        val fragment = CrimeFragment.newInstance(crimeId)
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