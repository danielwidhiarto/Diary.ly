package edu.bluejack23_1.diaryly.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import edu.bluejack23_1.diaryly.R


class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<User>
    private lateinit var imagelist: Array<Int>
    private lateinit var userList: Array<String>
    private lateinit var searchView: SearchView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        searchView = view.findViewById(R.id.searchBar)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search when the user submits a query
                if (!query.isNullOrEmpty()) {
                    getData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search as the user types (optional)
                return true
            }
        })
    }

    private fun getData(username: String) {
        // Clear the existing data list
        dataList.clear()

        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val username = document.getString("username") ?: ""
                    val userProfileImagePath = document.getString("userProfileImagePath") ?: ""

//                    // Create a UserModel object and add it to the list
//                    val userModel = UserModel(username, userProfileImagePath)
//                    dataList.add(userModel)
                }

                // Update the RecyclerView
                recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.e("SearchFragment", "Error getting user data: $exception")
            }
    }
}
