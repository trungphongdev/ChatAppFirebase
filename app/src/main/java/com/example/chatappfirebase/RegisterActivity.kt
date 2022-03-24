package com.example.chatappfirebase

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.newFixedThreadPoolContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.*
import java.util.jar.Manifest

class RegisterActivity : AppCompatActivity() {
    val TAG = this.javaClass.simpleName.toString()

    companion object {
        const val REQUEST_CODE_CAMERA = 123
        const val CAMERA_PERMISSION = 1234
        var imgUrl: Uri? = null
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database : FirebaseDatabase
    var selectPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.title = "Register"
        // setup
        auth = Firebase.auth         // auth = FirebaseAuth.getInstance()
        storage = Firebase.storage
        database =  Firebase.database

        register_button.setOnClickListener {
            createUser(
                email_edittext_register.text.toString(),
                password_edittext_register.text.toString()
            )

        }
        account_imageview_register.setOnClickListener {
            popUpMenu(it)
        }
        account_textview_register.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        using_phone_number_texview.setOnClickListener {
            startActivity(Intent(this,RegisterPhoneNumberActivity::class.java))
        }
    }


    //reciever
    private val getPictureGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        it?.let {
            selectPhotoUri = it
            account_imageview_register.setImageURI(it)
        }
    }


    private fun popUpMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.menu_popup_select_img, popup.menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.itemCamera -> {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, REQUEST_CODE_CAMERA)
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(android.Manifest.permission.CAMERA),
                            CAMERA_PERMISSION
                        )
                    }

                }
                R.id.itemGallery -> {
                    getPictureGallery.launch("image/*")
                }
                else -> {
                    TODO("")
                }
            }
            true
        })
        popup.show()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            } else {
                Toast.makeText(
                    this,
                    "You dennied your camera ! you can allow camera to settings",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val bitmap = data?.extras?.get("data") as Bitmap
                account_imageview_register.setImageBitmap(bitmap)
                val byte = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byte)
                val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
                val uri = Uri.parse(path)
                selectPhotoUri = uri
            }else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
            }
        }
        }

        private fun createUser(email: String, password: String) {
            if (email.isEmpty() || password.isEmpty() || selectPhotoUri == null) {
                Toast.makeText(this, "Input All Text Field", Toast.LENGTH_LONG).show()
                return
            }
            progressbar_register.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(layout_register, "Register Successed! Wait Syncing....", Snackbar.LENGTH_SHORT)
                            .show()
                        uploadImageToFireBaseStore()
                        Log.d(TAG,"Task Create User Success")

                    }
                    else {
                        return@addOnCompleteListener
                    }

                }.addOnFailureListener {
                Log.d(TAG, "Task Create User Failure" + it.message)
            }
        }

        private fun uploadImageToFireBaseStore() {
            if (selectPhotoUri == null) return
            val filename = auth.currentUser?.uid
            val ref = storage.getReference("images/").child(filename!!)
            ref.putFile(selectPhotoUri!!).addOnSuccessListener {
                Log.d(TAG, "Put File Image To Storage Success +${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "Download Image From Stoarage: $it")
                    saveUsertoFireBaseDataBase(it.toString())
                    imgUrl = it
                }.addOnFailureListener {
                    Log.d(TAG,"Put File Image To Storage Fauilure ${it.message}")
                }
            }
        }

    private fun saveUsertoFireBaseDataBase(uri: String) {
        val uid = auth.currentUser?.uid!!
        val user = User(uid,user_edittext_register.text.toString(),uri,true)
        database.getReference("users/").child(uid).setValue(user).addOnSuccessListener {
            Log.d(TAG,"Save User To Database Success")
            progressbar_register.visibility = View.GONE
            Thread.sleep(1000)
            val intent = Intent(this@RegisterActivity,MessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }.addOnFailureListener {
            Log.d(TAG,"Save User To Database Failure ${it.message}")
        }
    }
}


