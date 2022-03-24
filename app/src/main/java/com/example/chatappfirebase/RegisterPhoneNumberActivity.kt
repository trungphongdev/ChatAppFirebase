package com.example.chatappfirebase

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.BitmapCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register_phone_number.*
import java.io.ByteArrayOutputStream

class RegisterPhoneNumberActivity : AppCompatActivity() {
    companion object {
        var imgUrl: Uri? = null
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var reference: DatabaseReference
    private var selectPhotoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_phone_number)
        auth = Firebase.auth
        storage = Firebase.storage
        reference = Firebase.database.reference
        register_button_phonenumber.setOnClickListener {
            if (checkValidate()) {
                val intent = Intent(this, VerifyPhoneActivity::class.java)
                intent.putExtra("phone_number", phone_edittext_register_phonenumber.text.toString())
                intent.putExtra("username",user_edittext_register_phonenumber.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this,"Wrong fields Check again!!",Toast.LENGTH_SHORT).show()
            }
        }
        account_image_register_phonenumber.setOnClickListener {
            getImageFromGallery()
        }
    }
    private fun checkValidate(): Boolean {
        if (user_edittext_register_phonenumber.text.toString().isEmpty()
            || phone_edittext_register_phonenumber.text.toString().isEmpty() || selectPhotoUri == null
            /*|| phone_edittext_register_phonenumber.text.toString().length != 9*/
        ) {
            return false
        }
        else {
            return true
        }
    }

    private fun convertDrawableToBitmapImg() {
        //val bitmap = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_account_default)
        val drawable = account_image_register_phonenumber.drawable
        val bitmapDrawable: BitmapDrawable = drawable as BitmapDrawable
        val bitmap = bitmapDrawable.bitmap
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver,bitmap,"title",null)
        selectPhotoUri = Uri.parse(path)
    }


    //reciever
    private val getPictureGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        it?.let {
            selectPhotoUri = it
            imgUrl = it
            account_image_register_phonenumber.setImageURI(it)
        }
    }
    private fun getImageFromGallery() {
        getPictureGallery.launch("image/*")
    }

}