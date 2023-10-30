package edu.bluejack23_1.diaryly.search

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import edu.bluejack23_1.diaryly.R

class DetailSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_search)

        val tvCurrentUsername = findViewById<TextView>(R.id.tvCurrentUsername)
        val imgProfile = findViewById<ImageButton>(R.id.imgProfile)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnTotalJournal = findViewById<Button>(R.id.btnTotalJournal)

        // Retrieve user data from intent extras
        val user = intent.getSerializableExtra("user") as UserModel

        // Populate the views with user data
        tvCurrentUsername.text = user.username

        // Load the profile image using Picasso
        val userProfileImage = user.image_url

        if (userProfileImage != null) {
            Picasso.get().load(userProfileImage).into(imgProfile)
        } else {
            // Handle the case where the profile image URL is not available
        }

        btnBack.setOnClickListener {
          finish()
        }
    }
}


