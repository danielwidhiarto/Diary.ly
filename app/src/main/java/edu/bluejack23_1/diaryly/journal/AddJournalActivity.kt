package edu.bluejack23_1.diaryly.journal

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.bluejack23_1.diaryly.R
import java.text.SimpleDateFormat
import java.util.Calendar


class AddJournalActivity : AppCompatActivity() {
    private lateinit var etJournalTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnDateJournal: Button
    private lateinit var rdbtnPrivate: RadioButton
    private lateinit var rdbtnPublic: RadioButton
    private lateinit var btnAddJournal: Button
    private lateinit var btnImageView: ImageButton
    private lateinit var btnBackButton: ImageView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private val imageStoragePath = "journal_images/"
    private var selectedImageUri: Uri? = null

    companion object {
        private const val IMAGE_PICKER_REQUEST_CODE = 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                // Set the selected image on the ImageButton or perform any other necessary action
                btnImageView.setImageURI(selectedImageUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_journal)

        btnImageView = findViewById(R.id.btnImage)
        etJournalTitle = findViewById(R.id.etJournal)
        etContent = findViewById(R.id.etContent)
        btnDateJournal = findViewById(R.id.btnDateJournal)
        rdbtnPrivate = findViewById(R.id.rdbtnPrivate)
        rdbtnPublic = findViewById(R.id.rdbtnPublic)
        btnAddJournal = findViewById(R.id.btnAddJournal)
        btnBackButton = findViewById(R.id.backButton)

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        storageReference = FirebaseStorage.getInstance().reference.child(imageStoragePath)

        // Get the current user's ID
        val userId = firebaseAuth.currentUser!!.uid


        btnDateJournal.setOnClickListener {
            showDatePickerDialog()
        }

        btnBackButton.setOnClickListener {
            finish()
        }

        btnImageView.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Action")
            builder.setItems(options) { dialog, item ->
                when (options[item]) {
                    "Take Photo" -> {
                        // Similar to your EditProfileActivity
                        // Launch camera intent
                    }

                    "Choose from Gallery" -> {
                        val galleryIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryIntent.type = "image/*"

                        val mimeTypes = arrayOf("image/jpeg", "image/png")
                        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

                        startActivityForResult(galleryIntent, IMAGE_PICKER_REQUEST_CODE)
                    }

                    "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }


        btnAddJournal.setOnClickListener {
            // Capture user input
            val title = etJournalTitle.text.toString()
            val content = etContent.text.toString()
            val date = btnDateJournal.text.toString()
            val visibility = if (rdbtnPrivate.isChecked) "Private" else "Public"
            val documentRef = FirebaseFirestore.getInstance().collection("journals").document()
            val journalId = documentRef.id

            // Create a Journal object or a data class based on your requirements
            if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, R.string.fillAllTheFields, Toast.LENGTH_SHORT)
                    .show()
            } else {
// Check if an image is selected
                if (selectedImageUri != null) {
                    // If an image is selected, upload it to Firebase Storage
                    val imageRef =
                        storageReference.child("journal_images/$journalId/journal_image.jpg")
                    imageRef.putFile(selectedImageUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            // Image upload was successful
                            // You can get the download URL for the image
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                // Create a Journal object with the image URL
                                val journal = Journal(
                                    journalId,
                                    title,
                                    date,
                                    content,
                                    imageUrl,
                                    visibility
                                )
                                // Save the journal to Firestore
                                saveJournalToFirestore(journal)
                            }
                        }
                } else {
                    // If no image is selected, directly save other journal details to Firestore
                    val journal = Journal(
                        journalId,
                        title,
                        date,
                        content,
                        "", // Empty string for image URL
                        visibility
                    )
                    saveJournalToFirestore(journal)
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { view, year, month, dayOfMonth ->
            // Update the EditText with the selected date
            val selectedDate = SimpleDateFormat("dd-MM-yyyy").format(calendar.time)
            btnDateJournal.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun saveJournalToFirestore(journal: Journal) {
        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        // Get the UID of the currently logged-in user
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        // Check if the user is authenticated
        if (userId != null) {
            // Reference to the "journals" collection
            val collectionRef = db.collection("journals")

            // Add a new document with the journal data
            val journalData = HashMap<String, Any>()

            val documentRef = collectionRef.document(journal.id)
            journalData["id"] = journal.id // Set the unique ID for the mood entry
            journalData["title"] = journal.title
            journalData["date"] = journal.date
            journalData["content"] = journal.content
            journalData["image"] = journal.image
            journalData["visibility"] = journal.visibility
            journalData["userId"] = userId

            collectionRef.add(journalData)
                .addOnSuccessListener { documentReference ->
                    // Document was added successfully
                    Toast.makeText(this, R.string.journalAddSuccess,Toast.LENGTH_SHORT).show()

                    finish()
                }
                .addOnFailureListener { e ->
                    // Handle any errors here
                    Toast.makeText(this, R.string.journalAddFailed, Toast.LENGTH_SHORT).show()
                }
        }
    }
}