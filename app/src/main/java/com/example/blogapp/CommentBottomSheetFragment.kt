package com.example.blogapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogapp.Model.Comment
import com.example.blogapp.adapter.CommentAdapter
import com.example.blogapp.databinding.FragmentCommentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCommentBottomSheetBinding
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var databaseReference: DatabaseReference
    private val comments = mutableListOf<Comment>()

    private var postId: String? = null

    companion object {
        private const val ARG_POST_ID = "post_id"

        fun newInstance(postId: String): CommentBottomSheetFragment {
            val fragment = CommentBottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_POST_ID, postId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString(ARG_POST_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchComments()

        binding.buttonPostComment.setOnClickListener {
            postComment()
        }
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter(comments)
        binding.recyclerViewComments.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewComments.adapter = commentAdapter
    }

    private fun fetchComments() {
        postId?.let { postId ->
            databaseReference = FirebaseDatabase.getInstance().reference
                .child("blogs").child(postId).child("comments")

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    for (childSnapshot in snapshot.children) {
                        val comment = childSnapshot.getValue(Comment::class.java)
                        comment?.let { comments.add(it) }
                    }
                    comments.reverse() // Show newest comments first
                    commentAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load comments", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun postComment() {
        val commentText = binding.editTextComment.text.toString().trim()
        if (commentText.isNotEmpty()) {
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid
            val userReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)

            // Fetch user details (name and profile image URL) from the database
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName =
                        snapshot.child("name").getValue(String::class.java) ?: "Anonymous"
                    val profileImage =
                        snapshot.child("profileImage").getValue(String::class.java) ?: ""

                    val commentId = databaseReference.push().key ?: return
                    val comment = Comment(
                        commentId = commentId,
                        userId = userId,
                        userName = userName,
                        profileImage = profileImage,
                        commentText = commentText,
                        timestamp = System.currentTimeMillis()
                    )

                    // Post the comment and update UI
                    postId?.let {
                        databaseReference.child(commentId).setValue(comment).addOnSuccessListener {
                            binding.editTextComment.text.clear()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Failed to post comment", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}
