package com.example.chatappfirebase.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatappfirebase.R
import com.example.chatappfirebase.User
import kotlinx.android.synthetic.main.item_row_message.view.*
import java.io.FilterReader

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    val TAG = this.javaClass.simpleName.toString()
    var list = ArrayList<User>()
    var listSearch = ArrayList<User>()

    var onClickMessageListener: ((User) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            Glide.with(itemView.account_message_img.context).load(user.profileImgUrl).into(itemView.account_message_img)
            itemView.name_user_message_txv.setText(user.username)
            if (user.online == true)
            {itemView.online_message_img.visibility = View.VISIBLE}
            else {itemView.online_message_img.visibility = View.INVISIBLE}
            itemView.setOnClickListener {
                onClickMessageListener?.invoke(user)
            }
        }
    }
    public fun filterList(filterList: ArrayList<User>) {
        list = filterList
        notifyDataSetChanged()
        
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_message,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return list.size

    }



 /*   override fun getFilter(): Filter {
       return  object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val listFilter = ArrayList<User>()
                if (constraint.toString().isEmpty() || constraint.toString() == null) {
                    Log.i(TAG,listSearch[0].username)
                    listFilter.addAll(listSearch)
                } else {
                    for (user in listSearch) {
                        if (user.username.lowercase().contains(constraint.toString().lowercase())) {
                            listFilter.add(user)
                        }
                    }
                }
                val result = FilterResults()
                result.values = listFilter
                return result
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults) {
                list.clear()
                list.addAll(results.values as Collection<User>)
                notifyDataSetChanged()
            }

        }
    }*/

}