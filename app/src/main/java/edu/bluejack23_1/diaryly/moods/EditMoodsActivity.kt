package edu.bluejack23_1.diaryly.moods

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.diaryly.R
import java.text.SimpleDateFormat
import java.util.Calendar

class EditMoodsActivity : AppCompatActivity() {
    private lateinit var timeBtn: Button
    private lateinit var dateBtn: Button
    private lateinit var chosenMoods: TextView
    private lateinit var etNotes: EditText
    private lateinit var saveButton: ImageButton
    private lateinit var backButton: AppCompatImageView
    private lateinit var deleteButton: ImageButton

    private var chosenMoodLevel: String = "Chosen Mood"

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_moods)

// Get the intent extras
        val time = intent.getStringExtra("time")
        val date = intent.getStringExtra("date")
//        val chosenMood = intent.getStringExtra("chosenMood")
        val notes = intent.getStringExtra("notes")



        dateBtn = findViewById(R.id.btnDateMoods)
        timeBtn = findViewById(R.id. btnTimeMoods)
        chosenMoods = findViewById(R.id.moods)
        etNotes = findViewById(R.id.etNotes)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)
        deleteButton = findViewById(R.id.deleteButton)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


// Update the respective views with the retrieved data
        timeBtn.text = time
        dateBtn.text = date
//        chosenMoods.text = chosenMood
        etNotes.setText(notes)

        findViewById<Button>(R.id.btnDateMoods).setOnClickListener {
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
                dateBtn.text = selectedDate
            }, year, month, day)

            datePickerDialog.show()
        }


        findViewById<Button>(R.id.btnTimeMoods).setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    // Handle the selected time here
                    val selectedTime = "$hourOfDay:$minute"
                    // Update the TextView or any other field with the selected time
                    timeBtn.text = selectedTime
                },
                hour,
                minute,
                false
            )

            timePickerDialog.show()

        }

        backButton.setOnClickListener {
            finish()
        }

        deleteButton.setOnClickListener {
            val moodId = intent.getStringExtra("MOOD_ID")

            if (moodId != null) {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setMessage("Are you sure you want to delete this mood entry?")
                alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
                    // Perform the delete operation
                    deleteMoodFromFirestore(moodId)
                }
                alertDialogBuilder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                alertDialogBuilder.create().show()
            } else {
                Toast.makeText(this, "No mood selected for deletion", Toast.LENGTH_SHORT).show()
            }
        }

        saveButton.setOnClickListener {

            if (chosenMoodLevel == "Chosen Mood") {
                Toast.makeText(this, "Please choose a mood before saving", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                // Get the user's ID
                val userId = user.uid

                // Get the values to save
                val date = dateBtn.text.toString()
                val time = timeBtn.text.toString()
                val chosenMood = chosenMoodLevel
                val notes = etNotes.text.toString()

                // Create a MoodEntry object
                val documentRef = FirebaseFirestore.getInstance().collection("moods").document()
                val moodId = documentRef.id

                val moodEntry = Moods(moodId, date, time, chosenMood, notes)

                saveMoodsToFirestore(moodEntry)
            }
        }
    }

    private fun saveMoodsToFirestore(moods: Moods) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            val collectionRef = db.collection("moods")

            // Check if there's an ID passed from the intent to update the mood
            val moodId = intent.getStringExtra("MOOD_ID")

            if (moodId != null) {
                val documentRef = collectionRef.document(moodId)

                val moodData = HashMap<String, Any>()
                moodData["id"] = moodId
                moodData["date"] = moods.date
                moodData["time"] = moods.time
                moodData["chosenMood"] = moods.chosenMood
                moodData["notes"] = moods.notes
                moodData["userId"] = userId

                documentRef.set(moodData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Mood updated in Firestore", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating mood: $e", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    fun onMoodLevelSelected(view: View) {
        val moodTextView = findViewById<TextView>(R.id.moods)

        when (view.id) {
            R.id.moods_level_1 -> chosenMoodLevel = "Very Sad"
            R.id.moods_level_2 -> chosenMoodLevel = "Sad"
            R.id.moods_level_3 -> chosenMoodLevel = "Neutral"
            R.id.moods_level_4 -> chosenMoodLevel = "Happy"
            R.id.moods_level_5 -> chosenMoodLevel = "Very Happy"
        }

        moodTextView.text = chosenMoodLevel
    }

    private fun deleteMoodFromFirestore(moodId: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("moods").document(moodId)

        docRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Mood deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting mood: $e", Toast.LENGTH_SHORT).show()
            }
    }


}
