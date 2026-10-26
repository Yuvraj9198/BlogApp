package com.example.blogapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.Model.User
import com.example.blogapp.databinding.ItemUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserAdapter(
    private val userList: List<User>,
    private val onAddFriendClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private val databaseReference: DatabaseReference = FirebaseDatabase
        .getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com").reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userName.text = user.name
            Glide.with(binding.profileImage.context)
                .load(user.profileImage)
                .circleCrop()
                .into(binding.profileImage)

            binding.addFriendButton.setOnClickListener {
                onAddFriendClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount() = userList.size
}
