package com.example.chatappfirebase.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.example.chatappfirebase.ChatLogActivity
import com.example.chatappfirebase.ChatMessage
import com.example.chatappfirebase.R
import com.example.chatappfirebase.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.item_chat_log_left.view.*
import kotlinx.android.synthetic.main.item_chat_log_right.view.*
import kotlinx.coroutines.newFixedThreadPoolContext
import java.text.SimpleDateFormat
import java.util.*

class ChatLogAdapter(
    val context: Context,
    val list: List<ChatMessage>,
    val recieverProfile: String,

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_RECIEVE = 1
    val ITEM_SENT = 2
    var myProfile: String? = ""
    val formatter = SimpleDateFormat("hh:mm")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_log_left, parent, false)
            return ViewHolderChatLeft(view)

        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_log_right, parent, false)
            return ViewHolderChatRight(view)
        }
    }

override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val currentMessage = list[position]
    if (holder.javaClass == ViewHolderChatRight::class.java) {
        val viewHolder = holder as ViewHolderChatRight
        viewHolder.bindChatRight(currentMessage)

    }
    if (holder.javaClass == ViewHolderChatLeft::class.java) {
        val viewHolder = holder as ViewHolderChatLeft
        viewHolder.bindChatLeft(currentMessage)
    }

}

override fun getItemCount(): Int {
    return list.size
}

override fun getItemViewType(position: Int): Int {
    val currenMessage = list[position]
    if (Firebase.auth.currentUser?.uid.equals(currenMessage.fromId)) {
        return ITEM_SENT
    } else {
        return ITEM_RECIEVE
    }
}

inner class ViewHolderChatRight(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindChatRight(chat: ChatMessage) {
        itemView.chat_log_right_edittext.text = chat.text
        val key = Firebase.database.getReference("/users/${Firebase.auth.currentUser?.uid}").get()
            .addOnSuccessListener {
                Glide.with(context)
                    .load(it.getValue(User::class.java)?.profileImgUrl!!)
                    .into(itemView.image_item_chat_log_right)
            }
            .addOnFailureListener {
                Log.i("firebase", "Error getting data", it)
            }
        itemView.chat_log_right_edittext.setOnClickListener {
            itemView.time_log_right_textview.apply {
                visibility = View.VISIBLE
                text = formatter.format(Date(chat.timestamp!!))
            }

        }

    }
}

inner class ViewHolderChatLeft(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindChatLeft(chat: ChatMessage) {
        itemView.chat_log_left_edittext.text = chat.text
        Glide.with(context)
            .load(recieverProfile).placeholder(R.drawable.ic_baseline_person_outline_24)
            .into(itemView.image_item_chat_log_left)

        itemView.chat_log_left_edittext.setOnClickListener {
            itemView.time_log_left_textview.apply {
                visibility = View.VISIBLE
                text = formatter.format(Date(chat.timestamp!!))
            }
        }
    }
}
}