package com.example.mygram.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygram.databinding.GroupItemBinding
import com.example.mygram.models.Group
import com.example.mygram.models.GroupChat
import com.example.mygram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupAdapter(var list: List<Group>, var onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<GroupAdapter.Vh>() {
    inner class Vh(var groupItemBinding: GroupItemBinding) :
        RecyclerView.ViewHolder(groupItemBinding.root) {

        lateinit var mList: ArrayList<GroupChat>
        lateinit var reference: DatabaseReference
        fun onBind(group: Group) {
            groupItemBinding.tvName.text = group.name
            groupItemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(group)
            }

            mList = ArrayList()
            reference = FirebaseDatabase.getInstance().getReference("groupChats")
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mList.clear()
                    val groupChats = snapshot.children
                    for (groupChat in groupChats) {
                        val chat = groupChat.getValue(GroupChat::class.java)
                        if (chat?.groupId == group.id) {
                            mList.add(chat!!)
                        }
                    }
                    if (mList.size == 0) {
                        groupItemBinding.last.text = "no messages yet"
                    } else {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser?.uid == mList[mList.size - 1].sender) {
                            groupItemBinding.last.text = "me: ${mList[mList.size - 1].message}"
                        } else {
                            reference = FirebaseDatabase.getInstance()
                                .getReference("users")
                            reference.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val children = snapshot.children
                                    for (child in children) {
                                        val user = child.getValue(User::class.java)
                                        if (user?.uid==mList[mList.size-1].sender){
                                            groupItemBinding.last.text =
                                                "${user?.name}: ${mList[mList.size - 1].message}"
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                        }

                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(GroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(group: Group)
    }
}