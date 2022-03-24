package com.example.chatappfirebase

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_update_profile.*
import java.time.LocalDate
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private var imgUrl: Uri? = null
    companion object {
        var imgUri: Uri? = null
    }
    var userCurrent: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        auth = Firebase.auth
        storage = Firebase.storage
        database = Firebase.database

        supportActionBar?.title = "Update Profile"

        change_image_acount.setOnClickListener {
            getImageFromGallery()
        }
        update_profile_button.setOnClickListener {
            updateProfile(change_user_name_edt.text.toString())
        }

    }


    private val getPictureGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()) {
        imgUrl = it
        change_image_acount.setImageURI(imgUrl)
    }
    // take Gallery
    private fun getImageFromGallery() {
        getPictureGallery.launch("image/*")
    }


    //
    private fun updateProfile(username: String) {
        if (username.isEmpty() || imgUrl == null || username.length < 4) return
        uploadImageToFirebaseStorage(username)

    }

   private fun uploadImageToFirebaseStorage(username: String) {

       // upload to firebase storage
       val fileName = auth.currentUser?.uid.toString()
       if(imgUrl == null) return
       val ref = storage.getReference("images/").child("${auth.currentUser?.uid}")
       ref.putFile(imgUrl!!).addOnSuccessListener {
          ref.downloadUrl.addOnSuccessListener {
               imgUri = it
               Log.d("uri", imgUri.toString())
               uploadtoDatabase(it.toString(),username)
           }
       }.addOnFailureListener {
           Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
       }

    }
    private fun uploadtoDatabase(uri: String,username:String) {
        val id = auth.currentUser?.uid
        database.getReference("users/").child(id!!).child("username").setValue(username).addOnSuccessListener {
            Toast.makeText(this,"Udate Profile Successfully",Toast.LENGTH_LONG).show()
        }
        database.getReference("users/").child(id!!).child("profileImgUrl").setValue(uri.toString()).addOnSuccessListener {
            Toast.makeText(this,"Udate Profile Successfully",Toast.LENGTH_LONG).show()
        }
        startActivity(Intent(this,MessagesActivity::class.java))
    }


}