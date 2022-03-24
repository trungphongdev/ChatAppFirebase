package com.example.chatappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.example.chatappfirebase.adapter.MessageAdapter
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.item_select_user.*

class MessagesActivity : AppCompatActivity() {
    val TAG = this.javaClass.simpleName.toString()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: MessageAdapter
    val list = ArrayList<User>()
    companion object {
        const val MESSAGE_KEY = "user_message"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        auth = Firebase.auth
        database = Firebase.database
        setSupportActionBar(myToolbar)
        if (auth.currentUser == null) {
            val intent = Intent(this@MessagesActivity, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
            database.getReference("users/").child("${auth.currentUser?.uid}").get()
                .addOnSuccessListener {
                    Log.d("id", "id user is " + auth.currentUser?.uid.toString())
                    Glide.with(this)
                        .load(it.getValue(User::class.java)?.profileImgUrl)
                        .placeholder(R.drawable.ic_baseline_person_outline_24)
                        .into(account_imageview_message)
                    name_user_textview_message.setText(it.getValue(User::class.java)?.username.toString())

        }
        adapter = MessageAdapter()
        rvMessages.adapter = adapter
        getUserFromDB()

//        val credential = EmailAuthProvider
//            .getCredential("phongtrung@gmail.com", "phongtrung")
//        auth.currentUser?.reauthenticate(credential)?.addOnSuccessListener {
//            Toast.makeText(this,auth.currentUser.,Toast.LENGTH_LONG).show()
//        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option_mess,menu)
        val searchView = menu?.findItem(R.id.item_search_view)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
               return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun filter(text: String?) {
        val filterList = ArrayList<User>()
        for (user in adapter.list) {
            if (user.username.lowercase().contains(text?.lowercase()!!)) {
                filterList.add(user)
            }
            if (filterList.isEmpty()) {
                Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
            }
            else adapter.filterList(filterList)
        }
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
                    auth.signOut()
                }
            R.id.item_update_profile -> {
                startActivity(Intent(this,UpdateProfileActivity::class.java))
            }
            R.id.item_change_password -> {
                startActivity(Intent(this,ChangePasswordActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserFromDB() {
        val reference = database.getReference("users/").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.list.clear()
                for (user in snapshot.children) {
                    if (user.getValue(User::class.java)?.uid.equals(auth.currentUser?.uid)) {
                        continue
                    }
                    adapter.list.add(user.getValue(User::class.java)!!)
                    adapter.notifyDataSetChanged()
                    Log.i(TAG,"list size user : "+list.size)
                }
                adapter.onClickMessageListener = {
                    val intent = Intent(this@MessagesActivity,ChatLogActivity::class.java)
                    intent.putExtra(NewMessageActivity.USER_KEY,it)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessagesActivity,error.message,Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onStart() {
        val ref = database.getReference("users/").child("${auth.currentUser?.uid}/online").setValue(true)
        super.onStart()
    }

    override fun onDestroy() {
        val ref = database.getReference("users/").child("${auth.currentUser?.uid}/online").setValue(false)
        super.onDestroy()
    }


}