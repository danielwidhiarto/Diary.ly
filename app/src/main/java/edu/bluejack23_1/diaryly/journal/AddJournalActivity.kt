package edu.bluejack23_1.diaryly.journal

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
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
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null
    private val imageStoragePath = "journal_images/"


    companion object {
        private const val IMAGE_PICKER_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AddJournalActivity.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                // Set the selected image on the ImageButton
                btnImageView.setImageURI(selectedImageUri)
            }
        } else if (requestCode == AddJournalActivity.CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // You can also set the captured image as the background of the ImageButton
            btnImageView.setImageBitmap(imageBitmap)
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

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        storageReference = FirebaseStorage.getInstance().reference.child(imageStoragePath)

        // Get the current user's ID
        val userId = firebaseAuth.currentUser!!.uid

        btnAddJournal.setOnClickListener {
            // Capture user input
            val title = etJournalTitle.text.toString()
            val content = etContent.text.toString()
            val date = btnDateJournal.text.toString()
            val visibility = if (rdbtnPrivate.isChecked) "Private" else "Public"
            // Create a Journal object or a data class based on your requirements
            if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            } else {
                // All required fields are filled; proceed to create and save the journal
                val journal = Journal(title, date, content, R.drawable.ic_launcher_background, visibility)
                saveJournalToFirestore(journal)
            }
        }

        btnDateJournal.setOnClickListener {
            showDatePickerDialog()
        }

        btnImageView.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Action")
            builder.setItems(options) { dialog, item ->
                when (options[item]) {
                    "Take Photo" -> {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (cameraIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(cameraIntent,
                                AddJournalActivity.CAMERA_REQUEST_CODE
                            )
                        } else {
                            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    "Choose from Gallery" -> {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryIntent.type = "image/*"

                        val mimeTypes = arrayOf("image/jpeg", "image/png")
                        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

                        startActivityForResult(galleryIntent,
                            AddJournalActivity.IMAGE_PICKER_REQUEST_CODE
                        )
                    }
                    "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
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
            journalData["title"] = journal.title
            journalData["date"] = journal.date
            journalData["content"] = journal.content
            journalData["image"] = journal.image
            journalData["visibility"] = journal.visibility
            journalData["userId"] = userId

            collectionRef.add(journalData)
                .addOnSuccessListener { documentReference ->
                    // Document was added successfully
                    Toast.makeText(this, "Journal added to Firestore", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    // Handle any errors here
                    Toast.makeText(this, "Error adding journal: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadImageToStorage(imageUri: Uri, documentId: String) {
        val fileReference = storageReference.child("$documentId.${getFileExtension(imageUri)}")

        fileReference.putFile(imageUri)
            .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                // Image upload was successful, you can get the image URL if needed
                taskSnapshot.storage.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        val imageUrl = downloadUrl.toString()
                        // Now you can save the image URL in the Firestore document
                        updateImageInFirestore(imageUrl, documentId)
                    }
            })
            .addOnFailureListener { e ->
                // Handle any errors here
                Toast.makeText(this, "Error uploading image: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateImageInFirestore(imageUrl: String, documentId: String) {
        val collectionRef = firestore.collection("journals")
        val documentRef = collectionRef.document(documentId)

        documentRef.update("image", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Journal with image added to Firestore", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                // Handle any errors here
                Toast.makeText(this, "Error updating image in Firestore: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getFileExtension(uri: Uri): String {
        val contentResolver: ContentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ?: "jpg"
    }
}
