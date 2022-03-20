package com.example.chatappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        actionBar?.title = "Login"

        login_button.setOnClickListener {
            val email = email_edittext_login.text.toString().trim()
            val password = password_edittext_login.text.toString().trim()
            signInLogin(email,password)
        }
        register_textview_login.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }

    private fun signInLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Snackbar.make(layout_login,"Login Successed",Snackbar.LENGTH_SHORT).show()
                startActivity(Intent(this,MessagesActivity::class.java))
                Log.d("tk",auth.currentUser?.uid!!)

            } else {
                Toast.makeText(this,"Login Failed!",Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }


        }
    }
}