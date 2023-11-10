package edu.bluejack23_1.diaryly.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import edu.bluejack23_1.diaryly.R
import edu.bluejack23_1.diaryly.authentication.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var tvCurrentUsername: TextView
    private lateinit var btnTotalJournal: Button
    private lateinit var imgProfile: ImageButton
    private lateinit var btnEditProfile: Button
    private lateinit var btnDeleteAccount: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        tvCurrentUsername = view.findViewById(R.id.tvCurrentUsername)
        btnTotalJournal = view.findViewById(R.id.btnTotalJournal)
        imgProfile = view.findViewById(R.id.imgProfile)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount)

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        fetchUserDataAndCountJournals()

        btnEditProfile.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }

        btnDeleteAccount.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Account")
            builder.setMessage("Are you sure you want to delete your account? This action is irreversible.")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val user = firebaseAuth.currentUser // Get the current user

                // Delete user data from Firestore
                firestore.collection("users").document(user!!.uid).delete()
                    .addOnSuccessListener {
                        Log.d("ProfileFragment", "User data has been deleted from Firestore.")

                        // After successfully deleting from Firestore, proceed to delete the authentication
                        user.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // User account has been deleted from authentication
                                Log.d("ProfileFragment", "User account has been deleted.")
                                Toast.makeText(this.context, "Account Successfully Deleted!", Toast.LENGTH_SHORT).show()

                                // Navigate to the login activity.
                                val intent = Intent(context, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                // User account could not be deleted from authentication
                                Log.w("ProfileFragment", "User account could not be deleted from authentication.", task.exception)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("ProfileFragment", "Error deleting user data from Firestore.", e)
                    }
            })
            builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                // Do nothing
            })
            builder.show()
        }

        return view
    }

    // Create a function to fetch user data and count journals
    private fun fetchUserDataAndCountJournals() {
        // Get the current user's ID
        val userId = firebaseAuth.currentUser!!.uid

        // Fetch the user's image URL from Firestore
        firestore.collection("users").document(userId).get().addOnCompleteListener { userTask ->
            if (userTask.isSuccessful) {
                val userDocument = userTask.result
                if (userDocument.exists()) {
                    val username = userDocument.getString("username")
                    tvCurrentUsername.text = username

                    // Get the user's image URL from Firestore
                    val imageUrl = userDocument.getString("image_url")

                    // Load and display the profile image using Picasso
                    if (!imageUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.default_profile_image) // Placeholder image
                            .error(R.drawable.default_profile_image) // Error image (if loading fails)
                            .into(imgProfile)
                    } else {
                        // If no image URL is available, you can set a default image
                        imgProfile.setImageResource(R.drawable.default_profile_image)
                    }
                } else {
                    Log.d("ProfileFragment", "No such document")
                }

                // Query the user's journals
                firestore.collection("journals")
                    .whereEqualTo("userId", userId) // Filter journals by user
                    .get()
                    .addOnSuccessListener { journalResult ->
                        val journalCount = journalResult.size()
                        btnTotalJournal.text = "Total Journals: $journalCount"
                    }
                    .addOnFailureListener { journalException ->
                        Log.e("ProfileFragment", "Error fetching journals", journalException)
                    }

            } else {
                Log.d("ProfileFragment", "Error fetching user data", userTask.exception)
            }
        }
    }
}


