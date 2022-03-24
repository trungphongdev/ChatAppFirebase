package com.example.chatappfirebase.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatappfirebase.MessagesActivity
import com.example.chatappfirebase.R
import com.example.chatappfirebase.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_select_user.view.*

class NewMessageAdapter(val context: Context, val list: MutableList<User>) : RecyclerView.Adapter<NewMessageAdapter.ViewHolder>() {

    companion object {
        var onClickItemView: ((User) -> Unit)? = null
    }
     class  ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun parent(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_select_user, parent, false)
                return ViewHolder(view)
            }
        }

         fun bind(user: User) {
             itemView.name_user_item_new_message.text = user.username.toString()
             Glide.with(itemView.image_item_new_message.context)
                 .load(user.profileImgUrl)
                 .placeholder(R.drawable.ic_baseline_person_outline_24)
                 .into(itemView.image_item_new_message)
             itemView.name_user_item_new_message.setOnClickListener {
                      onClickItemView?.invoke(user)
             }

         }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.parent(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
       return  list.size
    }
}