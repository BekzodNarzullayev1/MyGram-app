package com.example.mygram.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygram.databinding.ChatItemLeftBinding
import com.example.mygram.databinding.ChatItemRightBinding
import com.example.mygram.models.Chat


class MessageAdapter(var list: List<Chat>, var currentUserUid:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ToVh(var chatItemLeftBinding: ChatItemLeftBinding) :
        RecyclerView.ViewHolder(chatItemLeftBinding.root) {

        fun onBind(chat: Chat) {
            chatItemLeftBinding.txtSend.text = chat.message
            chatItemLeftBinding.time.text = chat.date
        }
    }

    inner class FromVh(var chatItemRightBinding: ChatItemRightBinding) :
        RecyclerView.ViewHolder(chatItemRightBinding.root) {

        fun onBind(chat: Chat) {
            chatItemRightBinding.txtSend.text = chat.message
            chatItemRightBinding.time.text = chat.date
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
                ChatItemLeftBinding.inflate(
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