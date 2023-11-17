package edu.bluejack23_1.diaryly.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.bluejack23_1.diaryly.HomeActivity
import edu.bluejack23_1.diaryly.R

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<TextView>(R.id.tvRegisterHere)
        val forgotPasswordButton = findViewById<TextView>(R.id.tvForgotPass)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validate the input fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sign in the user with Firebase
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User signed in successfully
                    Toast.makeText(this, "User signed in successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    // User sign in failed
                    Toast.makeText(this, "User sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerButton.setOnClickListener {
            // Navigate to the register page
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
