package com.example.mygram.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygram.databinding.ChatItemLeftBinding
import com.example.mygram.databinding.ChatItemRightBinding
import com.example.mygram.databinding.GroupChatItemLeftBinding
import com.example.mygram.models.Chat
import com.example.mygram.models.GroupChat
import com.example.mygram.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class GroupMessageAdapter(var list: List<GroupChat>,var currentUserUid:String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ToVh(var groupChatItemLeftBinding: GroupChatItemLeftBinding) :
        RecyclerView.ViewHolder(groupChatItemLeftBinding.root) {

        fun onBind(groupChat: GroupChat) {
            val reference = FirebaseDatabase.getInstance().getReference("users").child(groupChat.sender!!)
            reference.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mesSender = snapshot.getValue(User::class.java)
                    groupChatItemLeftBinding.name.text = mesSender?.name
                    Picasso.get().load(mesSender?.photoUrl).into(groupChatItemLeftBinding.profileImg)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            groupChatItemLeftBinding.txtSend.text = groupChat.message
            groupChatItemLeftBinding.time.text = groupChat.date
        }
    }

    inner class FromVh(private var chatItemRightBinding: ChatItemRightBinding) :
        RecyclerView.ViewHolder(chatItemRightBinding.root) {

        fun onBind(groupChat: GroupChat) {
            chatItemRightBinding.txtSend.text = groupChat.message
            chatItemRightBinding.time.text = groupChat.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return FromVh(
                ChatItemRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return ToVh(
                GroupChatItemLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            val fromVh = holder as FromVh
            fromVh.onBind(list[position])
        } else {
            val toVh = holder as ToVh
            toVh.onBind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].sender == currentUserUid) {
            return 1
        }
        return 2
    }
}