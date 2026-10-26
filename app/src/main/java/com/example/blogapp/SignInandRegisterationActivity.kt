package com.example.blogapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.UserData
import com.example.blogapp.databinding.ActivitySignInandRegisterationBinding
import com.example.blogapp.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SignInandRegisterationActivity : AppCompatActivity() {
    private val binding: ActivitySignInandRegisterationBinding by lazy {
        ActivitySignInandRegisterationBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1 
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://blogapp-edac6-default-rtdb.firebaseio.com")
        storage = FirebaseStorage.getInstance()

        //for visibility of field
        val action = intent.getStringExtra("action")
        if (action == "login") {
            binding.loginEmail.visibility = View.VISIBLE
            binding.loginPassword.visibility = View.VISIBLE
            binding.registerbutton.isEnabled = false
            binding.registerbutton.alpha = 0.5f
            binding.loginbutton.visibility = View.VISIBLE
            binding.registerNewhere.isEnabled = false
            binding.registerNewhere.alpha = 0.5f
            binding.registerName.visibility = View.GONE
            binding.registerEmail.visibility = View.GONE
            binding.registerPassword.visibility = View.GONE
            binding.cardView.visibility = View.GONE


            binding.loginbutton.setOnClickListener {
                val loginEmail = binding.loginEmail.text.toString()
                val loginPassword = binding.loginPassword.text.toString()

                if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
                    Toast.makeText(this, "Please Fill All The Details", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "login successfull", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "please fill correct details",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }

        } else if (action == "register") {
            binding.loginbutton.isEnabled = false
            binding.loginbutton.alpha = 0.5f


        }
        binding.registerbutton.setOnClickListener {
            // Get data from edit text
            val registerName = binding.registerName.text.toString()
            val registerEmail = binding.registerEmail.text.toString()
            val registerPassword = binding.registerPassword.text.toString()
            if (registerName.isEmpty() || registerPassword.isEmpty() || registerEmail.isEmpty())
                Toast.makeText(this, "Please Fill All The Details ", Toast.LENGTH_SHORT).show()
            else {
                auth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user: FirebaseUser? = auth.currentUser
                            auth.signOut()
                            user?.let {
                                // save user data in firebase realtime database

                                val userReference: DatabaseReference =
                                    database.getReference("Users")
                                val userId: String = user.uid
                                val userData = UserData(
                                    registerName, registerEmail
                                )
                                userReference.child(userId).setValue(userData)
                                //upload image to fire base storage
                                val storageReference: StorageReference =
                                    storage.reference.child("profile_image/$userId.jpg")
                                storageReference.putFile(imageUri!!).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        storageReference.downloadUrl.addOnCompleteListener { imageUri ->
                                            if (imageUri.isSuccessful){
                                                val imageUrl = imageUri.result.toString()
                                                // save the image url to the data base
                                                userReference.child(userId).child("profileImage")
                                                    .setValue(imageUrl)

                                            }
                                        }
                                    }
                                }
                                Toast.makeText(
                                    this,
                                    "User Register Succesfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, WelcomeActivity::class.java))
                                finish()

                            }

                        } else {
                            Toast.makeText(this, "User Registeration Failed", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }
            }
        }

        //set on click listener to chose image
        binding.cardView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select Image"), PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null)
            imageUri = data.data
        Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform())
            .into(binding.userRegisterImage)
    }
}
