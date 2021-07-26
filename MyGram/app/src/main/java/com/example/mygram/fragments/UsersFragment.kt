package com.example.mygram.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.mygram.MessageActivity
import com.example.mygram.adapters.UserAdapter
import com.example.mygram.databinding.FragmentUsersBinding
import com.example.mygram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UsersFragment : Fragment() {

    lateinit var binding: FragmentUsersBinding
    lateinit var userAdapter: UserAdapter
    lateinit var userList: ArrayList<User>
    lateinit var firebaseUser: FirebaseUser
    lateinit var reference: DatabaseReference
    lateinit var firebaseAuth: FirebaseAuth
//    var isOnline:Boolean?=false
//    lateinit var user:User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        userList = ArrayList()

        readUsers()

        return binding.root
    }

    private fun readUsers() {


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("users")
        val currentUser = firebaseAuth.currentUser

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(User::class.java)
                    if (value != null && value.uid != currentUser?.uid) {
                        userList.add(value)
                    }
                }
                userAdapter =
                    UserAdapter(userList, true, object : UserAdapter.OnItemClickListener {
                        override fun onItemClick(user: User, position: Int) {
                            val intent = Intent(requireContext(), MessageActivity::class.java)
                            intent.putExtra("userId", user.uid)
                            startActivity(intent)
                        }
                    })
                binding.rv.adapter = userAdapter
//                binding.rv.addItemDecoration(
//                    DividerItemDecoration(
//                        requireContext(),
//                        RecyclerView.VERTICAL
//                    )
//                )
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}