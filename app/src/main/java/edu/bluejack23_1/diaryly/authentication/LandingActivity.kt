package edu.bluejack23_1.diaryly.authentication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import edu.bluejack23_1.diaryly.R

class LandingActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        // Check and request camera permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermission(
                Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        // Check and request notification permission
        checkAndRequestPermission(
            Manifest.permission.VIBRATE, NOTIFICATION_PERMISSION_REQUEST_CODE
        )

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

    private fun checkAndRequestPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this, arrayOf(permission), requestCode
            )
        }
    }

    // Handle permission request results
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission granted
                    Log.d("Permissions", "Camera permission granted")
                } else {
                    // Camera permission denied
                    Log.d("Permissions", "Camera permission denied")
                }
            }

            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Notification permission granted
                    Log.d("Permissions", "Notification permission granted")
                } else {
                    // Notification permission denied
                    Log.d("Permissions", "Notification permission denied")
                }
            }
        }
    }
}