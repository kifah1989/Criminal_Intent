package com.example.criminalintent

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.security.auth.callback.Callback

class LoginFragment : Fragment() {
    interface Callbacks {
        fun onLogin()
        fun onLogOut()
    }
    private var callbacks: Callbacks? = null
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginBtn: Button
    private lateinit var auth: FirebaseAuth
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        emailField = view.findViewById(R.id.email_field) as EditText
        passwordField = view.findViewById(R.id.password_field) as EditText
        loginBtn = view.findViewById(R.id.login_btn) as Button
        return view
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!=null){
            callbacks?.onLogin()
        }

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        loginBtn.setOnClickListener {
            loginBtn.isEnabled = false
            auth.signInWithEmailAndPassword(
                emailField.text.toString(),
                passwordField.text.toString()
            )
                .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "log in successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            val currentUser = auth.currentUser
                            if(currentUser!=null){
                                callbacks?.onLogin()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }
    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}