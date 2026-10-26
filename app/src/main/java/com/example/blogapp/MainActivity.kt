package com.example.blogapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.adapter.BlogAdapter
import com.example.blogapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private val blogItems = mutableListOf<BlogItemModel>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //saved blog page


        binding.saveArticleButton.setOnClickListener {
            startActivity(Intent(this,SavedArticlesActivity::class.java))
        }

        binding.profileImage.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }
        binding.cardView2.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }
        auth = FirebaseAuth.getInstance()
        databaseReference =
            FirebaseDatabase.getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com").reference.child(
                "blogs"
            )
        // set user profile
        val userId: String? = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfileImage(userId)
        }
        // set blog post

        // initialize recycler view and set adapter
        val recyclerView = binding.blogRecyclerView
        val blogAdapter = BlogAdapter(blogItems)
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        //fetch data from DB
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                blogItems.clear()
                for (snapshot in snapshot.children) {
                    val blogItem = snapshot.getValue(BlogItemModel::class.java)
                    if (blogItem != null) {
                        blogItems.add(blogItem)
                    }
                }
                //reverse the list
                blogItems.reverse()
                // notify the adapter  that the data has changed
                blogAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Blog loading failed", Toast.LENGTH_SHORT).show()

            }
        })
        binding.floatingAddArticleButton.setOnClickListener {
            startActivity(Intent(this, AddArticleActivity::class.java))

        }

    }

    private fun loadUserProfileImage(userId: String) {
        val userReference: DatabaseReference =
            FirebaseDatabase.getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com").reference.child(
                "Users"
            ).child(userId)
        userReference.child("profileImage").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl: String? = snapshot.getValue(String::class.java)
                if (profileImageUrl != null) {
                    Glide.with(this@MainActivity).load(profileImageUrl).into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error Loading Profile Image", Toast.LENGTH_SHORT)
                    .show()

            }
        })
    }
}