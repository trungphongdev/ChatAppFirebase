package com.example.chatappfirebase

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_register_phone_number.*
import kotlinx.android.synthetic.main.activity_verify_phone.*
import java.util.*
import java.util.concurrent.TimeUnit

class VerifyPhoneActivity : AppCompatActivity() {
    val TAG = "Check"
    var verificationID: String? = null
    var userId: String? = null
    companion object {
        val phoneNumberTest = "+1 1212121111"
        val OTPCode = "123321"
        var urlImg: Uri? = null
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: StorageReference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        auth = Firebase.auth
        storage = Firebase.storage.reference
        database = Firebase.database.reference


        val phoneNumber = intent.getStringExtra("phone_number")
        val username = intent.getStringExtra("username")
        Log.d(TAG,"url" + RegisterPhoneNumberActivity.imgUrl.toString())

        if (phoneNumber != null) {
            sendVertificationCode(phoneNumber)
        }
        verify_otp_button.setOnClickListener {
            val code = otp_phoneNumber_edittext.text.toString()
            if(code.isEmpty()) {
               return@setOnClickListener
            }
            verify_otp_progressbar.visibility = View.VISIBLE
            verifyCode(code)

        }
    }

    private fun sendVertificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            //.setPhoneNumber("+84"+phoneNumber)
            .setPhoneNumber(phoneNumberTest)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onCodeSent(verificationId: String,
                                        forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, forceResendingToken)
                    verificationID = verificationId
                    Log.d(TAG,"otp id" + verificationID)
                }
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    val code = phoneAuthCredential.smsCode
                    Log.d(TAG,"sms code" + code)
                    if (code != null) {
                        otp_phoneNumber_edittext.setText(code)
                        verify_otp_progressbar.visibility = View.VISIBLE
                        verifyCode(code)
                    }

                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@VerifyPhoneActivity, e.message.toString(),Toast.LENGTH_LONG).show()
                }

            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun verifyCode(code: String) {
            val credential = PhoneAuthProvider.getCredential(verificationID!!, code)
            signInUserByCridentials(credential)
    }

    private fun signInUserByCridentials(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    userId = task.result.user?.uid
                    Log.d(TAG,"uid user" + userId)
                    uploadImageToFirebaseStorage()

                } else {
                    Toast.makeText(this,"Sign In Error",Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun uploadImageToFirebaseStorage() {
        val fileName = auth.currentUser?.uid
        storage.child("/images/$fileName").putFile(RegisterPhoneNumberActivity.imgUrl!!).addOnSuccessListener {
            storage.child("images/$fileName").downloadUrl.addOnSuccessListener {
                urlImg = it
                Log.d("Check", urlImg.toString())
                saveUsertoFireBaseDataBase(urlImg.toString())
            }

        }
    }

    private fun saveUsertoFireBaseDataBase(urlImg: String) {
        val username = intent.getStringExtra("username")
        val user = User(userId!!,username!!,urlImg)
        database.child("/users/$userId").setValue(user).addOnSuccessListener {
            startActivity(Intent(this,MessagesActivity::class.java))
            Log.d("Check","Success")
        }.addOnFailureListener {
            Log.d("Check",it.message.toString())
        }
    }
}