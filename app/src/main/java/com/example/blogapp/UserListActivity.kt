package com.example.blogapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.Model.User
import com.example.blogapp.adapter.UserAdapter
import com.example.blogapp.databinding.ActivityUserListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var databaseReference: DatabaseReference
    private val userList = mutableListOf<User>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchUserList()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(userList) { user ->
            addFriend(user)
        }
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsers.adapter = userAdapter
    }

    private fun fetchUserList() {
        // Set up reference to the "Users" node in Firebase
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        // Add a listener to fetch the list of users from the database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                // Iterate through each user and add them to the list, excluding the current user
                for (childSnapshot in snapshot.children) {
                    val user = childSnapshot.getValue(User::class.java)
                    if (user != null && user.userId != currentUserId) {
                        userList.add(user)
                    }
                }

                // Notify the adapter that data has changed
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserListActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addFriend(user: User) {
        if (currentUserId == null) {
            Toast.makeText(this, "No user is currently logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId!!)

        // Check if the friend already exists in the current user's friends list
        userReference.child("friends").child(user.userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // Friend doesn't exist, proceed to add
                    userReference.child("friends").child(user.userId).setValue(true)
                        .addOnSuccessListener {
                            Toast.makeText(this@UserListActivity, "${user.name} added as a friend", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@UserListActivity, "Failed to add ${user.name} as a friend", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Friend already exists
                    Toast.makeText(this@UserListActivity, "${user.name} is already your friend", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserListActivity, "Error checking friend status: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
