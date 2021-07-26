package com.example.mygram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygram.adapters.MessageAdapter
import com.example.mygram.databinding.ActivityMessageBinding
import com.example.mygram.models.Chat
import com.example.mygram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMessageBinding
    lateinit var reference: DatabaseReference
    lateinit var messageAdapter: MessageAdapter
    lateinit var mList: ArrayList<Chat>
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this,MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }

        binding.rv.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        binding.rv.layoutManager = linearLayoutManager

        val intent = intent
        val userId = intent.getStringExtra("userId")
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User::class.java)
                binding.username.text = user?.name
                Picasso.get().load(user?.photoUrl).into(binding.profileImg)
                //readMessages(currentUser?.uid!!,userId)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.btnSend.setOnClickListener {
            val message = binding.txtSend.text.toString()
            val date = Date()
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
            val currentDate = simpleDateFormat.format(date)
            if (message != "") {
                sendMessage(currentUser?.uid!!, userId, message,currentDate)
            } else {
                Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show()
            }
            binding.txtSend.setText("")
        }

        mList = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                val children = snapshot.children
                for (child in children) {
                    val chat = child.getValue(Chat::class.java)
                    if (chat?.receiver == currentUser?.uid && chat?.sender == userId ||
                        chat?.receiver == userId && chat.sender == currentUser?.uid
                    ) {
                        mList.add(chat)
                    }
                }
                messageAdapter = MessageAdapter(mList, currentUser?.uid!!)
                binding.rv.adapter = messageAdapter

                binding.rv.smoothScrollToPosition(mList.size)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun sendMessage(sender: String, receiver: String, message: String,date:String) {
        reference = FirebaseDatabase.getInstance().reference
        val hashMap = HashMap<String, String>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        hashMap["date"] = date

        reference.child("chats").push().setValue(hashMap)
    }

    private fun readMessages(myId: String, userId: String) {
        mList = ArrayList()
        val currentUser = firebaseAuth.currentUser

        reference = FirebaseDatabase.getInstance().getReference("chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (child in snapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat?.receiver == myId && chat.sender == userId ||
                        chat?.receiver == userId && chat.sender == myId
                    ) {
                        mList.add(chat)
                    }
                    messageAdapter = MessageAdapter(mList, currentUser?.uid!!)
                    binding.rv.adapter = messageAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun status(status:String){
        val currentUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("users").child(currentUser?.uid!!)
        val hashMap = HashMap<String,Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}