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
import com.squareup.picasso.Picasso
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
    private lateinit var backbtn: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var storageReference: StorageReference
    private val imageStoragePath = "journal_images/"
    private var selectedImageUri: Uri? = null

    companion object {
        private const val IMAGE_PICKER_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EditJournalActivity.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                // Set the selected image on the ImageButton or perform any other necessary action
                btnImage.setImageURI(selectedImageUri)
            }
        }
    }

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

        storageReference = FirebaseStorage.getInstance().reference.child(imageStoragePath)

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
            showImagePickerDialog()
        }

        backbtn.setOnClickListener() {
            finish()
        }

        // Set click listener for the "Edit Journal" button
        btnAddJournal.setOnClickListener {
            // Call a function to update the journal data
            updateJournal(journalId)
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Action")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> {
                    // Open the camera to capture an image
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
                }

                "Choose from Gallery" -> {
                    // Open the gallery to choose an image
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryIntent.type = "image/*"
                    startActivityForResult(galleryIntent, IMAGE_PICKER_REQUEST_CODE)
                }

                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
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
                        val imageUrl = documentSnapshot.getString("image")

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

                        // Load and display the image using Picasso or any other image loading library
                        if (!imageUrl.isNullOrEmpty()) {
                            // Load and display the image using Picasso or your preferred library
                            Picasso.get()
                                .load(imageUrl)
                                .placeholder(R.drawable.default_profile_image) // Placeholder image
                                .error(R.drawable.default_profile_image) // Error image (if loading fails)
                                .into(btnImage)
                        } else {
                            // If no image URL is available, you can set a default image or hide the ImageView
                            btnImage.setImageResource(R.drawable.default_profile_image)
                            // Alternatively, you can hide the ImageView if you don't want to show a default image
                            // btnImage.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the failure, e.g., show an error message
                }
        }
    }

    private fun updateJournal(journalId: String?) {
        // Check if the journalId is not null...
        if (journalId != null) {
            // Retrieve the edited data...
            val editedTitle = etJournalTitle.text.toString().trim()
            val editedContent = etContent.text.toString().trim()
            val editedDate = btnDateJournal.text.toString()

            // Add validation for empty fields
            if (editedTitle.isEmpty() || editedContent.isEmpty() || editedDate.isEmpty()) {
                Toast.makeText(this, R.string.fillAllTheFields, Toast.LENGTH_SHORT).show()
                return
            }

            val editedVisibility = if (rdbtnPrivate.isChecked) "Private" else "Public"

            // You may implement logic for handling image upload here...
            // Upload the image (if selected) and get the image URL...
            uploadImageAndSaveJournal(
                journalId,
                editedTitle,
                editedContent,
                editedDate,
                editedVisibility
            )
            finish()
        }
    }


    private fun uploadImageAndSaveJournal(
        journalId: String,
        editedTitle: String,
        editedContent: String,
        editedDate: String,
        editedVisibility: String
    ) {
        // Check if an image is selected...
        if (selectedImageUri != null) {
            // Upload the image to Firebase Storage...
            val imageRef = storageReference.child("$journalId/profile_image.jpg")
            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Image upload was successful...
                    // You can get the download URL for the image...
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        // Now you can update the journal data in Firestore, including the image URL...
                        updateJournalDataWithImage(
                            journalId,
                            editedTitle,
                            editedContent,
                            editedDate,
                            editedVisibility,
                            imageUrl
                        )
                    }
                }
        } else {
            // No image selected, update the journal data without the image URL...
            updateJournalData(journalId, editedTitle, editedContent, editedDate, editedVisibility)
        }
    }

    private fun updateJournalData(
        journalId: String,
        editedTitle: String,
        editedContent: String,
        editedDate: String,
        editedVisibility: String
    ) {
        // Update the data in Firestore without the image URL...
        firestore.collection("journals").document(journalId)
            .update(
                "title", editedTitle,
                "content", editedContent,
                "date", editedDate,
                "visibility", editedVisibility
            )
            .addOnSuccessListener {
                // Handle the success, e.g., show a success message...
            }
            .addOnFailureListener { exception ->
                // Handle the failure, e.g., show an error message...
            }
    }

    private fun updateJournalDataWithImage(
        journalId: String,
        editedTitle: String,
        editedContent: String,
        editedDate: String,
        editedVisibility: String,
        imageUrl: String
    ) {
        firestore.collection("journals").document(journalId)
            .update(
                "title", editedTitle,
                "content", editedContent,
                "date", editedDate,
                "visibility", editedVisibility,
                "image", imageUrl
            )
            .addOnSuccessListener {
                // Handle the success, e.g., show a success message...
            }
            .addOnFailureListener { exception ->
                // Handle the failure, e.g., show an error message...
            }
    }
}
