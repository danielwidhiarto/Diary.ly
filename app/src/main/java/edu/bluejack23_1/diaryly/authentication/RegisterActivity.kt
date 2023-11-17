package edu.bluejack23_1.diaryly.authentication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack23_1.diaryly.R
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val usernameEditText = findViewById<EditText>(R.id.etUsername)
        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.etConfirmPassword)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginHereTextView = findViewById<TextView>(R.id.tvLoginhere)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Validate the input fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Pattern.matches("^[a-zA-Z0-9_]+$", username)) {
                Toast.makeText(this, "Username must not contain spaces.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.length < 3 || username.length > 30) {
                Toast.makeText(
                    this,
                    "Username must be between 3 and 30 characters.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new user account
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User account created successfully
                        Toast.makeText(
                            this,
                            "User account created successfully.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val userId = firebaseAuth.currentUser!!.uid // Get the user's UID

                        // Add the user data to Firestore along with the userId and default image URL
                        val user = hashMapOf(
                            "userId" to userId,
                            "username" to username,
                            "email" to email,
                            "image_url" to ""
                        )
                        firestore.collection("users").document(userId).set(user)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // User data added successfully.
                                    Toast.makeText(
                                        this,
                                        "User data added successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Upload the default profile image to Firebase Storage
                                    uploadDefaultProfileImage(userId)
                                } else {
                                    // User data not added successfully.
                                    Toast.makeText(
                                        this,
                                        "User data not added successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        // User account creation failed
                        Toast.makeText(this, "User account creation failed.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            loginHereTextView.setOnClickListener {
                // Navigate to the login activity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun uploadDefaultProfileImage(userId: String) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("profile_images/$userId.jpg")

        // Get the Bitmap from the VectorDrawable
        val defaultImageDrawable = resources.getDrawable(R.drawable.default_profile_image, null)
        val defaultImageBitmap = Bitmap.createBitmap(
            defaultImageDrawable.intrinsicWidth,
            defaultImageDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(defaultImageBitmap)
        defaultImageDrawable.setBounds(0, 0, canvas.width, canvas.height)
        defaultImageDrawable.draw(canvas)

        // Convert the Bitmap to a byte array
        val defaultImageBytes = ByteArrayOutputStream().apply {
            defaultImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
        }.toByteArray()

        // Upload the default image to Firebase Storage
        imageRef.putBytes(defaultImageBytes)
            .addOnSuccessListener {
                // Image upload was successful
                // Get the download URL for the image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Update the profileImage field in the user's Firestore document
                    firestore.collection("users").document(userId)
                        .update("profileImage", uri.toString())
                        .addOnSuccessListener {
                            // Profile image URL saved successfully
                            Toast.makeText(
                                this,
                                "Default profile image uploaded.",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Navigate to the login activity
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { exception ->
                            // Handle the failure to save the profile image URL
                            Toast.makeText(
                                this,
                                "Failed to save profile image URL.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the failure to upload the default image
                Toast.makeText(this, "Failed to upload default profile image.", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
