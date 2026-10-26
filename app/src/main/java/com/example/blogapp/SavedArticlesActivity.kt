package com.example.blogapp

import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.widget.Adapter
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.adapter.BlogAdapter
import com.example.blogapp.databinding.ActivitySavedArticlesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SavedArticlesActivity : AppCompatActivity() {
    private val binding: ActivitySavedArticlesBinding by lazy {
        ActivitySavedArticlesBinding.inflate(layoutInflater)
    }
    private val savedBlogsArticle = mutableListOf<BlogItemModel>()
    private lateinit var blogAdapter: BlogAdapter
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // initialize adapter
        blogAdapter = BlogAdapter(savedBlogsArticle.filter { it.isSaved }.toMutableList())

        val recyclerView = binding.savedarticlerecyclerview
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userId = auth.currentUser?.uid
        if (userId != null) {

            val userReference =
                FirebaseDatabase.getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com")
                    .getReference("Users").child(userId).child("saveBlogPosts")
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val postId: String? = postSnapshot.key
                        val isSaved = postSnapshot.value as Boolean
                        if (postId != null && isSaved) {

                            // fetch the corresponding blog item posted by postId usiong courutine
                            CoroutineScope(Dispatchers.IO).launch {
                                val blogItem = fetchBlogItem(postId)
                                if (blogItem != null){
                                    savedBlogsArticle.add(blogItem)
                                    launch ( Dispatchers.Main ){
                                        blogAdapter.updateData(savedBlogsArticle)
                                    }
                                }
                            }
                        }

                    }


                }


                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private suspend fun fetchBlogItem(postId: String): BlogItemModel? {
        val blogReference =
            FirebaseDatabase.getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com")
                .getReference("blogs")

        return try {

            val dataSnapshot= blogReference.child(postId).get().await()
            val  blogData=dataSnapshot.getValue(BlogItemModel::class.java)
            blogData
        }catch (e:Exception){
            null
        }

        }
    }