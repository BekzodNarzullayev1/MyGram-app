package com.example.mygram.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mygram.MessageActivity
import com.example.mygram.adapters.UserAdapter
import com.example.mygram.databinding.FragmentChatsBinding
import com.example.mygram.models.Chat
import com.example.mygram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId


class ChatsFragment : Fragment() {

    lateinit var binding: FragmentChatsBinding
    lateinit var list: ArrayList<User>
    lateinit var userListId: ArrayList<String>

    lateinit var reference: DatabaseReference
    lateinit var userAdapter: UserAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(layoutInflater)

        val currentUser = FirebaseAuth.getInstance().currentUser

        list = ArrayList()
        userListId = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userListId.clear()
                for (child in snapshot.children) {
                    val chat = child.getValue(Chat::class.java)
                    if (chat?.sender == currentUser?.uid) {
                        userListId.add(chat?.receiver!!)
                    }
                    if (chat?.receiver == currentUser?.uid) {
                        userListId.add(chat?.sender!!)
                    }
                }

                readChats()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        updateToken(FirebaseInstanceId.getInstance().token!!)

        return binding.root
    }

    private fun readChats() {
        list = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)

                    for (id in userListId) {
                        if (user?.uid == id) {
                            if (list.size != 0) {
                                for (user1: User in list) {
                                    if (user.uid != user1.uid) {
                                        list.add(user)
                                    }
                                }
                            } else {
                                list.add(user)
                            }
                        }
                    }
                }

                userAdapter = UserAdapter(list, true,object : UserAdapter.OnItemClickListener {
                    override fun onItemClick(user: User, position: Int) {
                        val intent = Intent(requireContext(), MessageActivity::class.java)
                        intent.putExtra("userId", user.uid)
                        startActivity(intent)
                    }

                })
                binding.rv.adapter = userAdapter

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateToken(token:String){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token1 = Token(token)
        reference.child(currentUser?.uid!!).setValue(token1)
    }

}