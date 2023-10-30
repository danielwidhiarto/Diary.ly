package edu.bluejack23_1.diaryly.journal

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.diaryly.R

class JournalDetailActivity : AppCompatActivity() {
    private lateinit var tvJournalTitle: TextView
    private lateinit var tvJournalDate: TextView
    private lateinit var tvJournalContent: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var journalReference: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_detail)

        // Initialize views
        tvJournalTitle = findViewById(R.id.tvJournalTitle)
        tvJournalDate = findViewById(R.id.tvJournalDate)
        tvJournalContent = findViewById(R.id.tvJournalContent)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Retrieve the journal document ID from intent
        val journalPath = intent.getStringExtra("journalId")

        if (journalPath != null) {
            Log.d("JournalDetailActivity", "journalPath: $journalPath")

            // Set Firestore document reference to the specific journal
            journalReference = firestore.document(journalPath)

            // Fetch and display journal data
            journalReference.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val journal = documentSnapshot.toObject(Journal::class.java)
                        if (journal != null) {
                            // Display journal data
                            tvJournalTitle.text = journal.title
                            tvJournalDate.text = journal.date
                            tvJournalContent.text = journal.content
                        }
                    } else {
                        // Handle the case where the journal document does not exist
                        // For example, display an error message or return to the previous screen
                        Toast.makeText(this, "Journal not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle errors
                }
        } else {
            Toast.makeText(this, "Journal ID not found", Toast.LENGTH_SHORT).show()
        }
    }
}