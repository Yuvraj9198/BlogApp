package com.example.blogapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.blogapp.databinding.ActivityProfileBinding
import com.example.blogapp.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com").reference.child("Users")

        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfileData(userId)
            loadUserCount()
        }
        binding.userlist.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }


        binding.addNewBlogButton.setOnClickListener {
            startActivity(Intent(this, AddArticleActivity::class.java))
        }

        binding.logoutbuttton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        binding.yourArticleButton.setOnClickListener {
            startActivity(Intent(this, ArticleActivity::class.java))
        }
    }

    private fun loadUserProfileData(userId: String) {
        val userReference: DatabaseReference = databaseReference.child(userId)

        userReference.child("profileImage").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if (profileImageUrl != null) {
                    Glide.with(this@ProfileActivity).load(profileImageUrl).into(binding.Userprofile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load profile image", Toast.LENGTH_SHORT).show()
            }
        })

        userReference.child("name").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                if (userName != null) {
                    binding.userName.text = userName
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadUserCount() {
        // Reference to the Users node in Firebase
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userCount = snapshot.childrenCount
                binding.userlist.text = userCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load user count", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
