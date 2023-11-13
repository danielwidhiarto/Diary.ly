package edu.bluejack23_1.diaryly.profile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import edu.bluejack23_1.diaryly.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var etUsername: EditText
    private lateinit var tvEmail: TextView
    private lateinit var etEmail: EditText
    private lateinit var tvPassword: TextView
    private lateinit var etPassword: EditText
    private lateinit var tvConfPass: TextView
    private lateinit var etConfirmPassword: EditText
    private lateinit var updateButton: Button
    private lateinit var imageButton: ImageButton
    private lateinit var backButton: ImageView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var storageReference: StorageReference  // Firebase Storage reference

    private var selectedImageUri: Uri? = null

    companion object {
        private const val IMAGE_PICKER_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                // Set the selected image on the ImageButton
                imageButton.setImageURI(selectedImageUri)
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // You can also set the captured image as the background of the ImageButton
            imageButton.setImageBitmap(imageBitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        tvUsername = findViewById(R.id.tvUsername)
        etUsername = findViewById(R.id.etUsername)
        tvEmail = findViewById(R.id.tvEmail)
        etEmail = findViewById(R.id.etEmail)
        tvPassword = findViewById(R.id.tvPassword)
        etPassword = findViewById(R.id.etPassword)
        tvConfPass = findViewById(R.id.tvConfPass)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        updateButton = findViewById(R.id.updateButton)
        imageButton = findViewById(R.id.imageButton)
        backButton = findViewById(R.id.backButton)

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Firebase Storage
        val storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        // Get the current user's ID
        val userId = firebaseAuth.currentUser!!.uid

        // Get the current user's data from Firestore
        firestore.collection("users").document(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val username = document.getString("username")
                    val email = document.getString("email")
                    val imageUrl = document.getString("image_url") // Fetch the image URL


//                    tvUsername.text = username
                    etUsername.setText(username)
//                    tvEmail.text = email
                    etEmail.setText(email)

                    // Load and display the profile image using Picasso
                    if (!imageUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.default_profile_image) // Placeholder image
                            .error(R.drawable.default_profile_image) // Error image (if loading fails)
                            .into(imageButton)
                    } else {
                        // If no image URL is available, you can set a default image
                        imageButton.setImageResource(R.drawable.default_profile_image)
                    }

                } else {
                    Log.d("EditProfileActivity", "No such document")
                }
            } else {
                Log.d("EditProfileActivity", "get failed with ", task.exception)
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        updateButton.setOnClickListener {
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update the user's data in Firestore
            val userDocument = firestore.collection("users").document(userId)
            val userData = mutableMapOf("username" to username, "email" to email)

            if (selectedImageUri != null) {
                val imageRef = storageReference.child("user_profile_images/$userId/profile_image.jpg")  // Set a path for the image in Storage
                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image upload was successful
                        // You can get the download URL for the image
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            // Now you can store imageUrl in Firestore, in the user's document
                            userData["image_url"] = imageUrl
                            userDocument.update(userData as Map<String, Any>).addOnSuccessListener {
                                // Continue with updating email and password if needed
                            }
                        }
                    }
            }

            userDocument.update(userData as Map<String, Any>).addOnSuccessListener {
                // Update successful

                firebaseAuth.currentUser!!.updateEmail(email).addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        // Update the user's password in Firebase Auth
                        firebaseAuth.currentUser!!.updatePassword(password).addOnCompleteListener { passwordTask ->
                            if (passwordTask.isSuccessful) {
                                Log.d("EditProfileActivity", "User email and password updated successfully.")
                                // Show the toast here
                                Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Log.d("EditProfileActivity", "User password update failed.", passwordTask.exception)
                            }
                        }
                    } else {
                        Log.d("EditProfileActivity", "User email update failed.", emailTask.exception)
                    }
                }

            }.addOnFailureListener { e ->
                Log.d("EditProfileActivity", "User data update failed.", e)
            }
        }

        imageButton.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Action")
            builder.setItems(options) { dialog, item ->
                when (options[item]) {
                    "Take Photo" -> {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (cameraIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
                        } else {
                            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    "Choose from Gallery" -> {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryIntent.type = "image/*"

                        val mimeTypes = arrayOf("image/jpeg", "image/png")
                        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

                        startActivityForResult(galleryIntent, IMAGE_PICKER_REQUEST_CODE)
                    }
                    "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

    }
}
