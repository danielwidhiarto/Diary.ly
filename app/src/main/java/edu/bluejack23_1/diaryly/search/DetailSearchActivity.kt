package edu.bluejack23_1.diaryly.search

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack23_1.diaryly.R

class DetailSearchActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_detail_search)

            val tvCurrentUsername = findViewById<TextView>(R.id.tvCurrentUsername)
            val imgProfile = findViewById<ImageButton>(R.id.imgProfile)
            val btnTotalJournal = findViewById<Button>(R.id.btnTotalJournal)

            // Retrieve user data from intent extras
            val user = intent.getSerializableExtra("user") as UserModel

            // Populate the views with user data
            tvCurrentUsername.text = user.username
            // Set other user details in the layout as needed
        }
    }
