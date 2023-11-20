package edu.bluejack23_1.diaryly.journal

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

class JournalFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var journalAdapter: JournalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data from Firestore
        val db = FirebaseFirestore.getInstance()
        val journalList = ArrayList<Journal>()

// Get the UID of the currently logged-in user
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        var journalAdapter: JournalAdapter? = null // Initialize the adapter as nullable

        if (userId != null) {
            val query = db.collection("journals")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)

                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Handle errors here
                        return@addSnapshotListener
                    }

                    journalList.clear() // Clear the existing list

                    for (document in snapshot!!) {
                        val title = document.getString("title") ?: "No Title"
                        val date = document.getString("date") ?: "No Date"
                        val content = document.getString("content") ?: "No Content"
                        val imageUrl =
                            document.getString("image") ?: "" // Get the image URL from Firestore
                        val visibility = document.getString("visibility") ?: "No Data"
                        val id = document.id // Retrieve the document ID

                        journalList.add(Journal(id, title, date, content, imageUrl, visibility))
                    }

                    // Initialize the adapter if it's null
                    if (journalAdapter == null) {
                        journalAdapter = JournalAdapter(journalList)
                        recyclerView.adapter = journalAdapter
                    } else {
                        // Notify the adapter that the data has changed
                        journalAdapter!!.notifyDataSetChanged()
                    }
                }
        }

        // Set an OnClickListener for the FAB
        val fabAddJournal = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fabAddJournal.setOnClickListener {
            val intent = Intent(requireContext(), AddJournalActivity::class.java)
            startActivity(intent)
        }
    }
}
