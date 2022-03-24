package com.example.chatappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.internal.Sleeper
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase

    private val TAG: String = this.javaClass.simpleName.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        auth = Firebase.auth
        storage = Firebase.storage
        database = Firebase.database

        supportActionBar?.title = "Change Password"
        change_password_button.setOnClickListener {
            val email = email_edittext_changepassword.text.toString()
            val newpassword = password_edittext_changepassword.text.toString()
            val repassword = repassword_edittext_changepassword.text.toString()
            if (checkValidation(email,newpassword,repassword)) {
                changePassword(newpassword)
            }
        }
        email_edittext_changepassword.addTextChangedListener {
            emailLayout.error = null
        }
        password_edittext_changepassword.addTextChangedListener {
            newPasswordLayout.error = null
        }
        repassword_edittext_changepassword.addTextChangedListener {
            rePasswordLayout.error = null
        }
    }

    private fun checkValidation(email: String, newpassword: String, repassword: String): Boolean {
        if (email.isEmpty()  || newpassword.isEmpty() || repassword.isEmpty()) {
            Toast.makeText(this, "Text Field Empty", Toast.LENGTH_LONG).show()
            return false
        }
        if (!newpassword.equals(repassword)) {
            newPasswordLayout.error = "Re - Password not the same"
            return false
        }
        if (!auth.currentUser?.email.equals(email)) {
            emailLayout.error = "Email not correct"
            return false
        }
        return true
    }


    private fun changePassword(password: String) {
        auth.currentUser!!.updatePassword(password).addOnCompleteListener {
            Log.i(TAG,"Change Password Success")
            Snackbar.make(layout_changePassword,"Updated Password Successfully",Snackbar.LENGTH_LONG).show()
            auth.signOut()
            Thread.sleep(2000)
            startActivity(Intent(this,LoginActivity::class.java))
        }.addOnFailureListener {
           Log.i(TAG,"Change Password Failure")
        }

    }
}