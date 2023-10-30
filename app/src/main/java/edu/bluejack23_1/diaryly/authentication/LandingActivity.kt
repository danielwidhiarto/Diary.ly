package edu.bluejack23_1.diaryly.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack23_1.diaryly.R

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Set click listener for the login button
        loginButton.setOnClickListener {
            // Navigate to the login page (replace LoginActivity::class.java with your login activity)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the register button
        registerButton.setOnClickListener {
            // Navigate to the register page (replace RegisterActivity::class.java with your register activity)
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}