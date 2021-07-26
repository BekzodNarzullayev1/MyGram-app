package com.example.mygram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.mygram.adapters.ViewPagerAdapter
import com.example.mygram.databinding.ActivityMainBinding
import com.example.mygram.fragments.ChatsFragment
import com.example.mygram.fragments.GroupsFragment
import com.example.mygram.fragments.UsersFragment
import com.example.mygram.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var firebaseUser: FirebaseUser
    lateinit var reference: DatabaseReference
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var fragmentList: ArrayList<Fragment>
    lateinit var titleList: ArrayList<String>
    var isOnline: Boolean? = false
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val currentUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("users").child(currentUser?.uid!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.username.text = user?.name
                Picasso.get().load(user?.photoUrl).into(binding.profileImg)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        loadData()


        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, fragmentList, titleList)

        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun loadData() {
        fragmentList = ArrayList()
//        fragmentList.add(ChatsFragment())
        fragmentList.add(UsersFragment())
        fragmentList.add(GroupsFragment())

        titleList = ArrayList()
//        titleList.add("Chats")
        titleList.add("Users")
        titleList.add("Groups")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                //googleSignInClient.signOut()
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(
                        this,
                        StartActivity::class.java
                    )
                )
                finish()
                return true
            }
        }
        return false
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        val hashMap = HashMap<String, Any>()
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