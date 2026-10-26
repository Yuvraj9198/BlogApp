package com.example.blogapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.Model.Comment
import com.example.blogapp.R
import com.example.blogapp.databinding.CommentItemBinding
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    inner class CommentViewHolder(private val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {
            binding.userName.text = comment.userName
            binding.commentText.text = comment.commentText

            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val commentDate = Date(comment.timestamp)
            binding.timestamp.text = sdf.format(commentDate)

            Glide.with(binding.profileImage.context)
                .load(comment.profileImage)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile1)
                .into(binding.profileImage)
        }
    }
}
