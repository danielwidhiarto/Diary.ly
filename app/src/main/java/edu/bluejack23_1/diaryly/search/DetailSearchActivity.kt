package edu.bluejack23_1.diaryly.search

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import edu.bluejack23_1.diaryly.R
import edu.bluejack23_1.diaryly.journal.Journal
import edu.bluejack23_1.diaryly.journal.JournalAdapter

class DetailSearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JournalAdapter
    private val journalList = ArrayList<Journal>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_search)

        val tvCurrentUsername = findViewById<TextView>(R.id.tvCurrentUsername)
        val imgProfile = findViewById<ImageButton>(R.id.imgProfile)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnTotalJournal = findViewById<Button>(R.id.btnTotalJournal)

        recyclerView = findViewById(R.id.recycler_view)
        adapter = JournalAdapter(ArrayList(journalList))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        var journalAdapter: JournalAdapter? = null // Initialize the adapter as nullable

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

        val firestore = FirebaseFirestore.getInstance()
        val journalsCollection = firestore.collection("journals")

        // Query to fetch only journals of the specific user
        journalsCollection.whereEqualTo("userId", user.userId)
            .whereEqualTo("visibility", "Public")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("title") ?: "No Title"
                    val date = document.getString("date") ?: "No Date"
                    val content = document.getString("content") ?: "No Content"
                    val imageUrl = document.getString("image") ?: ""
                    val visibility = document.getString("visibility") ?: "No Data"
                    val id = document.id

                    journalList.add(Journal(id, title, date, content, imageUrl, visibility))
                }

                // Initialize the adapter if it's null
                if (journalAdapter == null) {
                    journalAdapter = JournalAdapter(journalList)
                    recyclerView.adapter = journalAdapter
                } else {
                    // Notify the adapter that the data has changed
                    journalAdapter!!.notifyDataSetChanged()
                }

                // Display the total number of journals
                val totalJournals = journalList.size
                btnTotalJournal.text = "Total Journals: $totalJournals"
            }
            .addOnFailureListener { exception ->
                // Handle errors here
            }
    }
}