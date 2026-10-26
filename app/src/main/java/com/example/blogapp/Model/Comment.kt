package com.example.blogapp.Model

data class Comment(
    val commentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val profileImage: String = "",
    val commentText: String = "",
    val timestamp: Long = 0
)
