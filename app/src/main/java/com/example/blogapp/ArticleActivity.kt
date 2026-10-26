package com.example.blogapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.adapter.ArticleAdapter
import com.example.blogapp.databinding.ActivityArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ArticleActivity : AppCompatActivity() {

    private val binding: ActivityArticleBinding by lazy { ActivityArticleBinding.inflate(layoutInflater) }
    private lateinit var databaseReference: DatabaseReference
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var blogAdapter: ArticleAdapter
    private lateinit var editBlogLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        setupRecyclerView()

        editBlogLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Blog edited successfully", Toast.LENGTH_SHORT).show()
            }
        }

        databaseReference = FirebaseDatabase.getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com")
            .getReference("blogs")

        fetchBlogData(currentUserId)
    }

    private fun setupRecyclerView() {
        blogAdapter = ArticleAdapter(this, emptyList(), object : ArticleAdapter.OnItemClickListener {
            override fun onEditClick(blogItem: BlogItemModel) {
                val intent = Intent(this@ArticleActivity, EditBlogActivity::class.java)
                intent.putExtra("blogItem", blogItem)
                editBlogLauncher.launch(intent)
            }

            override fun onReadMoreClick(blogItem: BlogItemModel) {
                val intent = Intent(this@ArticleActivity, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItem)
                startActivity(intent)
            }

            override fun onDeleteClick(blogItem: BlogItemModel) {
                deleteBlogPost(blogItem)
            }
        })

        binding.ArticleRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ArticleActivity)
            adapter = blogAdapter
        }
    }

    private fun fetchBlogData(currentUserId: String) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val blogSavedList = ArrayList<BlogItemModel>()
                for (postSnapshot in snapshot.children) {
                    val blogSaved = postSnapshot.getValue(BlogItemModel::class.java)
                    if (blogSaved != null && blogSaved.userId == currentUserId) {
                        blogSavedList.add(blogSaved)
                    }
                }
                blogAdapter.setData(blogSavedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ArticleActivity, "Error loading saved blogs", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteBlogPost(blogItem: BlogItemModel) {
        blogItem.postId?.let { postId ->
            databaseReference.child(postId).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Blog post deleted successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to delete blog post", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
