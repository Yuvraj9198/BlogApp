package com.example.blogapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityEditBlogBinding
import com.google.firebase.database.FirebaseDatabase

class EditBlogActivity : AppCompatActivity() {

    private val binding: ActivityEditBlogBinding by lazy {
        ActivityEditBlogBinding.inflate(layoutInflater)
    }

    companion object {
        const val EDIT_BLOG_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val blogItemModel = intent.getParcelableExtra<BlogItemModel>("blogItem")

        blogItemModel?.let {
            binding.blogTitle.editText?.setText(it.heading)
            binding.blogDescription.editText?.setText(it.post)
        }

        binding.SaveBlogButton.setOnClickListener {
            val updatedTitle = binding.blogTitle.editText?.text.toString().trim()
            val updatedDescription = binding.blogDescription.editText?.text.toString().trim()

            if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                blogItemModel?.apply {
                    heading = updatedTitle
                    post = updatedDescription
                    updateDataInFirebase(this)
                }
            }
        }
    }

    private fun updateDataInFirebase(blogItemModel: BlogItemModel) {
        val databaseReference = FirebaseDatabase
            .getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com")
            .getReference("blogs")

        val postId = blogItemModel.postId
        if (!postId.isNullOrEmpty()) {
            databaseReference.child(postId).setValue(blogItemModel)
                .addOnSuccessListener {
                    Toast.makeText(this, "Blog updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Blog update failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid blog post ID", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_BLOG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Handle activity result if needed
        }
    }
}
