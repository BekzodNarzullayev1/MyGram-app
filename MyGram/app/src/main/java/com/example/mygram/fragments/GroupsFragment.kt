package com.example.mygram.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.mygram.MessagesOfGroupActivity
import com.example.mygram.R
import com.example.mygram.adapters.GroupAdapter
import com.example.mygram.databinding.FragmentGroupsBinding
import com.example.mygram.models.Group
import com.example.mygram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupsFragment : Fragment() {

    lateinit var binding: FragmentGroupsBinding
    lateinit var reference: DatabaseReference
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var groupList: ArrayList<Group>
    lateinit var userList: List<User>
    lateinit var groupAdapter:GroupAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGroupsBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        groupList = ArrayList()
        userList = ArrayList()

        binding.addBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog,null,false)
            val groupName = dialogView.findViewById<EditText>(R.id.name_tv)
            val saveBtn = dialogView.findViewById<Button>(R.id.save)
            builder.setView(dialogView)

            val alertDialog = builder.create()
            alertDialog.show()


            saveBtn.setOnClickListener {
                val name = groupName.text.toString()
                val id = System.currentTimeMillis().toString()
                reference = FirebaseDatabase.getInstance().getReference("groups")
                reference.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val group = Group(id,name)
                        reference.child(id).setValue(group)
                        alertDialog.hide()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }

        }

        reference = FirebaseDatabase.getInstance().getReference("groups")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                val groups = snapshot.children
                for (group in groups) {
                    val group = group.getValue(Group::class.java)
                    groupList.add(group!!)
                }
                groupAdapter = GroupAdapter(groupList,object :GroupAdapter.OnItemClickListener{
                    override fun onItemClick(group: Group) {
                        val intent = Intent(requireContext(),MessagesOfGroupActivity::class.java)
                        intent.putExtra("groupId",group.id)
                        intent.putExtra("userId",currentUser?.uid)
                        startActivity(intent)
                    }
                })
                binding.rv.adapter = groupAdapter
                binding.rv.addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        RecyclerView.VERTICAL
                    )
                )
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return binding.root
    }

}