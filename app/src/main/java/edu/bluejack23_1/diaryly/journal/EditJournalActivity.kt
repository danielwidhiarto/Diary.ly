package edu.bluejack23_1.diaryly.journal
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.diaryly.R
import java.text.SimpleDateFormat
import java.util.Calendar

class EditJournalActivity : AppCompatActivity() {

    private lateinit var etJournalTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnDateJournal: Button
    private lateinit var btnImage: ImageButton // Include ImageButton if you have an image
    private lateinit var rdbtnPrivate: RadioButton
    private lateinit var rdbtnPublic: RadioButton
    private lateinit var btnAddJournal: Button
    private lateinit var backbtn : ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_journal)

        // Initialize UI elements
        etJournalTitle = findViewById(R.id.etJournal)
        etContent = findViewById(R.id.etContent)
        btnDateJournal = findViewById(R.id.btnDateJournal)
        btnImage = findViewById(R.id.btnImage)
        rdbtnPrivate = findViewById(R.id.rdbtnPrivate)
        rdbtnPublic = findViewById(R.id.rdbtnPublic)
        btnAddJournal = findViewById(R.id.btnEditJournal)
        backbtn = findViewById(R.id.backButton)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Retrieve the document ID from the Intent
        val journalId = intent.getStringExtra("journalId")

        // Fetch and show the data from Firestore
        fetchJournalData(journalId)

        findViewById<Button>(R.id.btnDateJournal).setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                // Update the EditText with the selected date
                val selectedDate = SimpleDateFormat("dd-MM-yyyy").format(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
                btnDateJournal.text = selectedDate
            }, year, month, day)

            datePickerDialog.show()
        }


        // Set click listener for the image button
        btnImage.setOnClickListener {
            // Implement your logic for handling image selection
            // For example, show an image picker dialog
        }

        backbtn.setOnClickListener() {
            finish()
        }

        // Set click listener for the "Edit Journal" button
        btnAddJournal.setOnClickListener {
            // Call a function to update the journal data
            updateJournal(journalId)

            finish()

//            // Redirect back to JournalDetailActivity without finishing EditJournalActivity
//            val intent = Intent(this, JournalDetailActivity::class.java)
//            // Pass the journalId to JournalDetailActivity if needed
//            intent.putExtra("journalsId", journalId)
//            startActivity(intent)


        }
    }

    private fun fetchJournalData(journalId: String?) {
        // Check if the journalId is not null
        if (journalId != null) {
            // Access the "journals" collection in Firestore
            val journalRef = firestore.collection("journals").document(journalId)

            // Fetch data from Firestore
            journalRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Retrieve data from the document
                        val title = documentSnapshot.getString("title")
                        val content = documentSnapshot.getString("content")
                        val date = documentSnapshot.getString("date")
                        val visibility = documentSnapshot.getString("visibility")

                        // Set the retrieved data to the UI elements
                        etJournalTitle.setText(title)
                        etContent.setText(content)
                        btnDateJournal.text = date

                        // Set the visibility based on the retrieved data
                        if (visibility == "Private") {
                            rdbtnPrivate.isChecked = true
                        } else if (visibility == "Public") {
                            rdbtnPublic.isChecked = true
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the failure, e.g., show an error message
                }
        }
    }

    private fun updateJournal(journalId: String?) {
        // Check if the journalId is not null
        if (journalId != null) {
            // Retrieve the edited data
            val editedTitle = etJournalTitle.text.toString()
            val editedContent = etContent.text.toString()
            val editedDate = btnDateJournal.text.toString()
            // You may implement logic for handling image upload here
            val editedVisibility = when {
                rdbtnPrivate.isChecked -> "Private"
                rdbtnPublic.isChecked -> "Public"
                else -> ""
            }

            // Update the data in Firestore
            firestore.collection("journals").document(journalId)
                .update(
                    "title", editedTitle,
                    "content", editedContent,
                    "date", editedDate,
                    "visibility", editedVisibility
                )
                .addOnSuccessListener {
                    // Handle the success, e.g., show a success message
                }
                .addOnFailureListener { exception ->
                    // Handle the failure, e.g., show an error message
                }
        }
    }
}
