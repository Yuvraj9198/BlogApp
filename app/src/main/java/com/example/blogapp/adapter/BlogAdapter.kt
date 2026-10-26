package com.example.blogapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.CommentBottomSheetFragment
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.R
import com.example.blogapp.ReadMoreActivity
import com.example.blogapp.databinding.BlogItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BlogAdapter(private val items: MutableList<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val databaseReference: DatabaseReference = FirebaseDatabase
        .getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com").reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BlogItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = items[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int = items.size

    inner class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blogItemModel: BlogItemModel) {
            val postId = blogItemModel.postId.orEmpty()
            val context = binding.root.context

            // Bind blog data to views
            binding.heading.text = blogItemModel.heading
            Glide.with(binding.profile.context).load(blogItemModel.profileImage).into(binding.profile)
            binding.userName.text = blogItemModel.userName
            binding.date.text = blogItemModel.date
            binding.post.text = blogItemModel.post
            binding.likecount.text = blogItemModel.likeCount.toString()

            // Open detailed blog view
            binding.root.setOnClickListener {
                val intent = Intent(context, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }

            // Handle Like Button
            handleLikeButton(postId, blogItemModel, binding)

            // Handle Save Button
            handleSaveButton(postId, blogItemModel, binding)

            // Handle Comment Button
            binding.postcommentbutton.setOnClickListener {
                if (currentUser != null) {
                    val commentBottomSheet = CommentBottomSheetFragment.newInstance(postId)
                    commentBottomSheet.show(
                        (context as androidx.fragment.app.FragmentActivity).supportFragmentManager,
                        commentBottomSheet.tag
                    )
                } else {
                    Toast.makeText(context, "You have to login first to comment", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun handleLikeButton(postId: String, blogItemModel: BlogItemModel, binding: BlogItemBinding) {
            val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")

            currentUser?.uid?.let { uid ->
                postLikeReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.likebutton.setImageResource(
                            if (snapshot.exists()) R.drawable.heartred else R.drawable.heartblack
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })

                // Handle like button click
                binding.likebutton.setOnClickListener {
                    if (currentUser != null) {
                        toggleLike(postId, blogItemModel, binding)
                    } else {
                        Toast.makeText(binding.root.context, "You have to login first", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun toggleLike(postId: String, blogItemModel: BlogItemModel, binding: BlogItemBinding) {
            binding.likebutton.isEnabled = false
            val userReference = databaseReference.child("Users").child(currentUser!!.uid)
            val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")

            postLikeReference.child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Unlike
                        userReference.child("likes").child(postId).removeValue().addOnSuccessListener {
                            postLikeReference.child(currentUser.uid).removeValue()
                            updateLikeCount(postId, blogItemModel, -1)
                            updateLikeButtonImage(binding, false)
                        }.addOnCompleteListener {
                            binding.likebutton.isEnabled = true
                        }
                    } else {
                        // Like
                        userReference.child("likes").child(postId).setValue(true).addOnSuccessListener {
                            postLikeReference.child(currentUser.uid).setValue(true)
                            updateLikeCount(postId, blogItemModel, 1)
                            updateLikeButtonImage(binding, true)
                        }.addOnCompleteListener {
                            binding.likebutton.isEnabled = true
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.likebutton.isEnabled = true
                }
            })
        }

        private fun updateLikeCount(postId: String, blogItemModel: BlogItemModel, delta: Int) {
            blogItemModel.likeCount += delta
            databaseReference.child("blogs").child(postId).child("likeCount").setValue(blogItemModel.likeCount)
        }

        private fun updateLikeButtonImage(binding: BlogItemBinding, liked: Boolean) {
            binding.likebutton.setImageResource(if (liked) R.drawable.heartred else R.drawable.heartblack)
        }

        private fun handleSaveButton(postId: String, blogItemModel: BlogItemModel, binding: BlogItemBinding) {
            val userReference = databaseReference.child("Users").child(currentUser!!.uid)
            val savePostReference = userReference.child("saveBlogPosts").child(postId)

            savePostReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.postsavebutton.setImageResource(
                        if (snapshot.exists()) R.drawable.savefilledred else R.drawable.save
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

            binding.postsavebutton.setOnClickListener {
                toggleSavePost(postId, blogItemModel, binding)
            }
        }

        private fun toggleSavePost(postId: String, blogItemModel: BlogItemModel, binding: BlogItemBinding) {
            val userReference = databaseReference.child("Users").child(currentUser!!.uid)
            val savePostRef = userReference.child("saveBlogPosts").child(postId)

            savePostRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Unsave post
                        savePostRef.removeValue().addOnSuccessListener {
                            blogItemModel.isSaved = false
                            binding.postsavebutton.setImageResource(R.drawable.save)
                            Toast.makeText(binding.root.context, "Blog Unsaved!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Save post
                        savePostRef.setValue(true).addOnSuccessListener {
                            blogItemModel.isSaved = true
                            binding.postsavebutton.setImageResource(R.drawable.savefilledred)
                            Toast.makeText(binding.root.context, "Blog saved", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    fun updateData(newItems: List<BlogItemModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
