package com.example.chatappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.item_select_user.*

class MessagesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        auth = Firebase.auth
        verifyUserLoggedIn()
        setSupportActionBar(myToolbar)
        Firebase.database.getReference("/users/${auth.currentUser?.uid}").get().addOnSuccessListener {
            Glide.with(this)
                .load(it.getValue(User::class.java)?.profileImgUrl)
                .placeholder(R.drawable.ic_baseline_person_outline_24)
                .into(account_imageview_message)
            name_user_textview_message.text = it.getValue(User::class.java)?.username

        }
    }

    private fun verifyUserLoggedIn() {
        super.onStart()
        val userId = auth.currentUser?.uid
        if (userId == null) {
            val intent = Intent(this@MessagesActivity, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option_mess,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_new_message -> {
                val intent = Intent(this@MessagesActivity, NewMessageActivity::class.java)
                startActivity(intent)

            }
                R.id.item_sign_out -> {
                    val intent = Intent(this@MessagesActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
        }
        return super.onOptionsItemSelected(item)
    }


}