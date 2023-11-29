package edu.bluejack23_1.diaryly.journal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.bluejack23_1.diaryly.R

class JournalDetailActivity : AppCompatActivity() {

    private lateinit var tvJournalTitle: TextView
    private lateinit var tvJournalDate: TextView
    private lateinit var tvJournalContent: TextView
    private lateinit var btnDelete: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnBack: ImageView

    private lateinit var journalId: String
    private lateinit var documentRef: DocumentReference
    private lateinit var snapshotListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_detail)

        tvJournalTitle = findViewById(R.id.tvJournalTitle)
        tvJournalDate = findViewById(R.id.tvJournalDate)
        tvJournalContent = findViewById(R.id.tvJournalContent)
        btnDelete = findViewById(R.id.btnDelete)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnBack = findViewById(R.id.backButton)

        journalId = intent.getStringExtra("journalsId") ?: ""
        val title = intent.getStringExtra("journalTitle")
        val content = intent.getStringExtra("journalContent")
        val date = intent.getStringExtra("journalDate")

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        documentRef = db.collection("journals").document(journalId)

        // Add a snapshot listener to the document
        snapshotListener = documentRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle errors here
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Get the updated data from the snapshot
                val title = snapshot.getString("title") ?: "No Title"
                val date = snapshot.getString("date") ?: "No Date"
                val content = snapshot.getString("content") ?: "No Content"

                // Update your UI with the new data
                tvJournalTitle.text = title
                tvJournalDate.text = date
                tvJournalContent.text = content
            } else {
                // Handle the case where the document no longer exists
                finish() // Close the activity if the document is deleted
            }
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Journal")
                .setMessage("Are you sure you want to delete this journal?")
                .setPositiveButton("Delete") { dialog, _ ->
                    val db = FirebaseFirestore.getInstance()

                    if (journalId != null) {
                        db.collection("journals")
                            .document(journalId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this, R.string.journalDeleteSuccess, Toast.LENGTH_SHORT
                                ).show()
                                finish() // Close the activity after deletion
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this, R.string.journalDeleteFailed, Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

        }

        btnUpdate.setOnClickListener {
            val intent = Intent(this, EditJournalActivity::class.java)

            // Assuming journalId, title, content, and date are retrieved from the intent
            intent.putExtra("journalId", journalId)
            intent.putExtra("journalTitle", title)
            intent.putExtra("journalContent", content)
            intent.putExtra("journalDate", date)

            startActivity(intent)
        }

        if (userId != null && journalId != null) {
            db.collection("journals")
                .document(journalId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val title = documentSnapshot.getString("title") ?: "No Title"
                    val date = documentSnapshot.getString("date") ?: "No Date"
                    val content = documentSnapshot.getString("content") ?: "No Content"

                    tvJournalTitle.text = title
                    tvJournalDate.text = date
                    tvJournalContent.text = content
                }
                .addOnFailureListener { exception ->
                    // Handle errors here
                }
        }

        // Set click listeners for buttons, implement delete and update actions
        // Link the fetched data with TextViews and handle delete/update functionality
    }

    override fun onDestroy() {
        // Remove the snapshot listener when the activity is destroyed to avoid memory leaks
        snapshotListener.remove()
        super.onDestroy()
    }
}
