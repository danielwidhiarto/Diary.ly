package edu.bluejack23_1.diaryly.moods
import android.os.Bundle
import android.view.View
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

class AddMoodsActivity : AppCompatActivity() {
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var chosenMoods: TextView
    private lateinit var etNotes: EditText
    private lateinit var saveButton: ImageButton
    private lateinit var backButton: AppCompatImageView

    private var chosenMoodLevel: String = "Chosen Mood"

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_moods)

        tvDate = findViewById(R.id.tvDate)
        tvTime = findViewById(R.id.tvTime)
        chosenMoods = findViewById(R.id.moods)
        etNotes = findViewById(R.id.etNotes)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        // Get the current date and time
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy") // You can change the date format as needed
        val date = simpleDateFormat.format(calendar.time)

        val timeFormat = SimpleDateFormat("HH:mm") // You can change the time format as needed
        val time = timeFormat.format(calendar.time)

        // Set the date and time in your TextViews
        tvDate.text = "$date"
        tvTime.text = "$time"

        (backButton as AppCompatImageView).setOnClickListener {
            finish()
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
                val date = tvDate.text.toString()
                val time = tvTime.text.toString()
                val chosenMood = chosenMoodLevel
                val notes = etNotes.text.toString()

                // Create a MoodEntry object
                val moodEntry = Moods(date, time, chosenMood, notes)

                saveMoodsToFirestore(moodEntry)
            }
        }
    }

    private fun saveMoodsToFirestore(moods: Moods) {
        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        // Get the UID of the currently logged-in user
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        // Check if the user is authenticated
        if (userId != null) {
            // Reference to the "journals" collection
            val collectionRef = db.collection("moods")

            // Add a new document with the Moods data
            val moodData = HashMap<String, Any>()
            moodData["date"] = moods.date
            moodData["time"] = moods.time
            moodData["chosenMood"] = moods.chosenMood
            moodData["notes"] = moods.notes
            moodData["userId"] = userId

            collectionRef.add(moodData)
                .addOnSuccessListener { documentReference ->
                    // Document was added successfully
                    Toast.makeText(this, "Moods added to Firestore", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    // Handle any errors here
                    Toast.makeText(this, "Error adding journal: $e", Toast.LENGTH_SHORT).show()
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


}