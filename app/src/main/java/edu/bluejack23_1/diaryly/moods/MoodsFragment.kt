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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import edu.bluejack23_1.diaryly.R

class MoodsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var moodsAdapter: MoodsAdapter
    private lateinit var snapshotListener: ListenerRegistration

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

        // Set up the snapshot listener for real-time updates
        val query = db.collection("moods")
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)

        snapshotListener = query.addSnapshotListener { snapshots, exception ->
            if (exception != null) {
                // Handle errors here
                return@addSnapshotListener
            }

            moodList.clear() // Clear the existing list

            for (document in snapshots!!) {
                val time = document.getString("time") ?: "No Title"
                val date = document.getString("date") ?: "No Date"
                val mood = document.getString("chosenMood") ?: "No Content"
                val notes = document.getString("notes") ?: "No Data"
                val id = document.id // Retrieve the document ID

                val moodEntry = Moods(id, date, time, mood, notes) // Update to include the ID
                moodList.add(moodEntry)
            }

            moodsAdapter.updateData(moodList) // Update adapter data
        }

        // Set an OnClickListener for the FAB
        val fabAddMood = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fabAddMood.setOnClickListener {
            val intent = Intent(requireContext(), AddMoodsActivity::class.java)
            startActivity(intent)
        }

    }

}
