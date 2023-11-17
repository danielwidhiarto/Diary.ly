package edu.bluejack23_1.diaryly.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.bluejack23_1.diaryly.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var btnSendLink: Button
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        etEmail = findViewById(R.id.etEmail)
        btnSendLink = findViewById(R.id.sendLinkButton)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            // Create an intent to start the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnSendLink.setOnClickListener {
            val email = etEmail.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the email is registered with Firebase.
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // The email is registered with Firebase.
                    // Send a password reset email to the user.
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "A password reset email has been sent to your email address.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Log.e(
                                "ForgotPasswordActivity",
                                "Failed to send password reset email: ${task.exception}"
                            )
                            Toast.makeText(
                                this,
                                "Failed to send password reset email. Please try again later.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // The email is not registered with Firebase.
                    Toast.makeText(
                        this, "The email address is not registered.", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}