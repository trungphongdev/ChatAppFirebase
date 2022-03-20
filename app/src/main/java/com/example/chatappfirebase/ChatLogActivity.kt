package com.example.chatappfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatappfirebase.adapter.ChatLogAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    var recieveRoom: String? = null
    var senderRoom: String? =null
    private lateinit var messageList: ArrayList<ChatMessage>
    private lateinit var messageAdapter: ChatLogAdapter
    var myProfile: String? = null
    var recieveProfile: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.username
        auth = Firebase.auth
        database = Firebase.database
        senderRoom = user?.uid + auth.currentUser?.uid
        recieveRoom = auth.currentUser?.uid + user?.uid
        messageList = ArrayList()

        messageAdapter = ChatLogAdapter(this,messageList,user?.profileImgUrl!!)

        recyclerview_chat_log.adapter = messageAdapter
        send_message_button.setOnClickListener {
            performMessage()
        }
        listenFromMessage()

    }


    private fun performMessage() {
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val fromId = auth.currentUser?.uid
        val toId = user?.uid
        if (fromId == null) return
        val text = chat_edittext.text.toString()
        val chatMessage = ChatMessage(text, fromId, toId,System.currentTimeMillis())
        val ref = database.getReference("/chats")
                   .child(senderRoom!!)
                    .child("messages").push()
            .setValue(chatMessage).addOnSuccessListener {
             database.getReference("/chats")
                .child(recieveRoom!!).
                 child("messages").push()
                 .setValue(chatMessage)

        }
        chat_edittext.text?.clear()
        recyclerview_chat_log.scrollToPosition(messageAdapter.itemCount -1)
    }

    private fun listenFromMessage() {
     val ref = database.getReference("/chats").child(senderRoom!!).child("messages")
         .addValueEventListener(object : ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 messageList.clear()
                 for (postsnapshot in snapshot.children) {
                     val message = postsnapshot.getValue(ChatMessage::class.java)
                     messageList.add(message!!)
                 }

                 messageAdapter.notifyDataSetChanged()
             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }

         })
    }
}