package com.example.blogapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityReadMoreBinding

class ReadMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val blogs = intent.getParcelableExtra<BlogItemModel>("blogItem")
        if (blogs != null) {
            // Retrieve data
            binding.titleText.text = blogs.heading
            binding.userName.text = blogs.userName
            binding.date.text = blogs.date
            binding.blogDescriptionTextView.text = blogs.post

            // Debug logging for the image URL
            val userImageUrl = blogs.profileImage
            if (!userImageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(userImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.profile) // Placeholder image
                    .error(R.drawable.profile1) // Error image if loading fails
                    .into(binding.profileImage)
            } else {
                Toast.makeText(this, "No profile image available", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Failed to load blog details", Toast.LENGTH_SHORT).show()
        }
    }
}
