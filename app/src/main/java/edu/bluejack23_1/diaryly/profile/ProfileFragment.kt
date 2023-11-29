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
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso
import edu.bluejack23_1.diaryly.R
import edu.bluejack23_1.diaryly.authentication.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var tvCurrentUsername: TextView
    private lateinit var btnTotalJournal: Button
    private lateinit var imgProfile: ImageButton
    private lateinit var btnEditProfile: Button
    private lateinit var btnDeleteAccount: Button
    private  lateinit var btnLogout : Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var documentRef: DocumentReference
    private lateinit var snapshotListener: ListenerRegistration


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
        btnLogout = view.findViewById(R.id.btnLogout)

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        fetchUserDataAndCountJournals()
        setUpSnapshotListener()

        btnEditProfile.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        btnDeleteAccount.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Account")
            builder.setMessage("Are you sure you want to delete your account? This action is irreversible.")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val user = firebaseAuth.currentUser // Get the current user

                // First, delete all journals related to the user
                firestore.collection("journals")
                    .whereEqualTo("userId", user!!.uid)
                    .get()
                    .addOnSuccessListener { journalResult ->
                        for (document in journalResult) {
                            firestore.collection("journals").document(document.id).delete()
                                .addOnSuccessListener {
                                    Log.d("ProfileFragment", "Journal deleted")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("ProfileFragment", "Error deleting journal", e)
                                }
                        }

                        // Then, delete the user data from Firestore
                        firestore.collection("users").document(user.uid).delete()
                            .addOnSuccessListener {
                                Log.d("ProfileFragment", "User data has been deleted from Firestore.")

                                // After successfully deleting from Firestore, proceed to delete the authentication
                                user.delete().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // User account has been deleted from authentication
                                        Log.d("ProfileFragment", "User account has been deleted.")

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
                    }
                    .addOnFailureListener { journalException ->
                        Log.e("ProfileFragment", "Error fetching journals", journalException)
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

        documentRef = firestore.collection("users").document(userId)

        // Fetch the user's image URL from Firestore
        documentRef.get().addOnCompleteListener { userTask ->
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

    private fun setUpSnapshotListener() {
        snapshotListener = documentRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle errors here
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Get updated data from the snapshot
                val username = snapshot.getString("username")
                tvCurrentUsername.text = username

                // Get the user's image URL from Firestore
                val imageUrl = snapshot.getString("image_url")

                // Load and display the profile image using Picasso
                if (!imageUrl.isNullOrEmpty()) {
                    Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.default_profile_image)
                        .error(R.drawable.default_profile_image)
                        .into(imgProfile)
                } else {
                    imgProfile.setImageResource(R.drawable.default_profile_image)
                }
            } else {
                Log.d("ProfileFragment", "No such document")
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.logout)
        builder.setMessage(R.string.logoutConfirm)
        builder.setPositiveButton(R.string.YesConfirm) { _, _ ->
            // Log out the user
            FirebaseAuth.getInstance().signOut()

            // Navigate to the login activity.
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish() // Optional: Finish the current activity to prevent going back to the profile screen
        }
        builder.setNegativeButton(R.string.NoConfirm, null)
        builder.show()
    }


    override fun onDestroyView() {
        // Remove the snapshot listener when the fragment is destroyed to avoid memory leaks
        snapshotListener.remove()
        super.onDestroyView()
    }
}


