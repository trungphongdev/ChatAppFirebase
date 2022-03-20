package com.example.chatappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatappfirebase.adapter.NewMessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_new_message.*
import java.util.function.LongFunction

class NewMessageActivity : AppCompatActivity() {
    private  lateinit var reference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var listUser: MutableList<User>
    companion object {
        const val USER_KEY = "User"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        reference = Firebase.database.reference
        auth = Firebase.auth
        listUser = mutableListOf()
        supportActionBar?.title = "Select User"
        fetchUsers()


    }

    private fun fetchUsers() {
        reference.child("/users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listUser.clear()
                for (user in snapshot.children) {
                    if (user.getValue(User::class.java)?.uid.equals(auth.currentUser?.uid)) {
                        continue
                    }
                    listUser.add(user.getValue(User::class.java)!!)
                }
                val adapter = NewMessageAdapter(this@NewMessageActivity,listUser)
                recyclerview_new_message.adapter = adapter
                NewMessageAdapter.onClickItemView = { user ->
                    val intent = Intent(this@NewMessageActivity, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,user)
                    startActivity(intent)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NewMessageActivity,"${error.message}",Toast.LENGTH_SHORT).show()
            }

        })
    }
}