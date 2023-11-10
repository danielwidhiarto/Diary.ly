package edu.bluejack23_1.diaryly.moods

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.bluejack23_1.diaryly.R

class MoodsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var moodsAdapter: MoodsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_moods, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        moodsAdapter = MoodsAdapter(ArrayList()) // Initialize adapter with an empty list
        recyclerView.adapter = moodsAdapter

        val db = FirebaseFirestore.getInstance()
        val moodList = ArrayList<Moods>()

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            db.collection("moods")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val time = document.getString("time") ?: "No Title"
                        val date = document.getString("date") ?: "No Date"
                        val mood = document.getString("chosenMood") ?: "No Content"
                        val notes = document.getString("notes") ?: "No Data"

                        val moodEntry = Moods(date, time, mood, notes)
                        moodList.add(moodEntry)
                    }

                    moodsAdapter.updateData(moodList) // Update adapter data

                }
                .addOnFailureListener { exception ->
                    // Handle errors here
                }
        }

        // Set an OnClickListener for the FAB
        val fabAddMood = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fabAddMood.setOnClickListener {
            val intent = Intent(requireContext(), AddMoodsActivity::class.java)
            startActivity(intent)
        }

//        // Find the "Edit" TextView
//        val tvEdit = view.findViewById<TextView>(R.id.tvEdit)
//        tvEdit.setOnClickListener {
//            // Handle the click event
//            val intent = Intent(requireContext(), EditMoodsActivity::class.java)
//            startActivity(intent)
//        }
    }

}
