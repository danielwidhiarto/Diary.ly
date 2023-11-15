package edu.bluejack23_1.diaryly.search

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import edu.bluejack23_1.diaryly.R
import edu.bluejack23_1.diaryly.journal.Journal
import edu.bluejack23_1.diaryly.journal.JournalAdapter

class DetailSearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JournalAdapter // Replace YourJournalAdapter with your actual adapter class
    private val publicJournalsList = mutableListOf<Journal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_search)

        val tvCurrentUsername = findViewById<TextView>(R.id.tvCurrentUsername)
        val imgProfile = findViewById<ImageButton>(R.id.imgProfile)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnTotalJournal = findViewById<Button>(R.id.btnTotalJournal)

        recyclerView = findViewById(R.id.recycler_view)
        adapter = JournalAdapter(ArrayList(publicJournalsList))
        recyclerView.adapter = adapter

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

        // Call the method to fetch and display public journals
        fetchPublicJournals()
    }


    private fun fetchPublicJournals() {
        val firestore = FirebaseFirestore.getInstance()
        val journalsCollection = firestore.collection("journals")

        // Query to fetch only public journals
        journalsCollection.whereEqualTo("visibility", "Public")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Convert the document to your Journal class (replace Journal with your actual data class)
                    val journal = document.toObject(Journal::class.java)
                    publicJournalsList.add(journal)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle the failure, e.g., show an error message
            }
    }

}


