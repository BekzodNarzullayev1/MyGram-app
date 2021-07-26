package com.example.mygram.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygram.databinding.UserItemBinding
import com.example.mygram.models.Chat
import com.example.mygram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserAdapter(
    private var list: List<User>,
    var isOnline: Boolean,
    var onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<UserAdapter.Vh>() {
    lateinit var mList: ArrayList<Chat>

    inner class Vh(var userItemBinding: UserItemBinding) :
        RecyclerView.ViewHolder(userItemBinding.root) {

        fun onBind(user: User, position: Int) {
            mList = ArrayList()
            val currentUser = FirebaseAuth.getInstance().currentUser
            val reference = FirebaseDatabase.getInstance().getReference("chats")
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mList.clear()
                    val chats = snapshot.children
                    for (chat in chats) {
                        val chat = chat.getValue(Chat::class.java)
                        if ((chat?.sender == user.uid && chat?.receiver == currentUser?.uid) || (chat?.receiver == user.uid && chat?.sender == currentUser?.uid)) {
                            mList.add(chat!!)
                        }
                    }
                    if (mList.size == 0) {
                        userItemBinding.last.text = "no message"
                    } else {
                        userItemBinding.last.text = mList[mList.size - 1].message
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            Picasso.get().load(user.photoUrl).into(userItemBinding.img)
            userItemBinding.username.text = user.name
            userItemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(user, position)
            }
            if (isOnline){
                if (user.status=="online"){
                    userItemBinding.imgOn.visibility = VISIBLE
                    userItemBinding.imgOff.visibility = GONE

                }else{
                    userItemBinding.imgOn.visibility = GONE
                    userItemBinding.imgOff.visibility = VISIBLE
                }
            }else{
                userItemBinding.imgOn.visibility = GONE
                userItemBinding.imgOff.visibility = GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(user: User, position: Int)
    }
}