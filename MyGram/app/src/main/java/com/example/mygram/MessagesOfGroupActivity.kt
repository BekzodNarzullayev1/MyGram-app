package com.example.mygram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygram.adapters.GroupMessageAdapter
import com.example.mygram.databinding.ActivityMessagesOfGroupBinding
import com.example.mygram.models.Group
import com.example.mygram.models.GroupChat
import com.example.mygram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessagesOfGroupActivity : AppCompatActivity() {
    lateinit var binding: ActivityMessagesOfGroupBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var reference: DatabaseReference
    lateinit var mList: ArrayList<GroupChat>
    lateinit var groupMessageAdapter:GroupMessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesOfGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.rv.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        binding.rv.layoutManager = linearLayoutManager

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val groupId = intent.getStringExtra("groupId")
        val userId = intent.getStringExtra("userId")

        reference = FirebaseDatabase.getInstance().getReference("groups").child(groupId!!)
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)
                binding.username.text = group?.name


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
                sendMessage(currentUser?.uid!!, groupId, message,currentDate)
            } else {
                Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show()
            }
            binding.txtSend.setText("")
        }

        mList = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("groupChats")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                val children = snapshot.children
                for (child in children) {
                    val groupChat = child.getValue(GroupChat::class.java)
                    if (groupChat?.groupId == groupId){
                        mList.add(groupChat)
                    }
                }
                groupMessageAdapter = GroupMessageAdapter(mList,currentUser?.uid!!)
                binding.rv.adapter = groupMessageAdapter
                binding.rv.smoothScrollToPosition(mList.size)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun sendMessage(sender:String,groupId:String,message:String,date:String){
        reference = FirebaseDatabase.getInstance().reference
        val hashMap = HashMap<String, String>()
        hashMap["sender"] = sender
        hashMap["groupId"] = groupId
        hashMap["message"] = message
        hashMap["date"] = date

        reference.child("groupChats").push().setValue(hashMap)
    }

}