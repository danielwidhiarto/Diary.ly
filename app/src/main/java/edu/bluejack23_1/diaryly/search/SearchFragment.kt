package edu.bluejack23_1.diaryly.search

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import edu.bluejack23_1.diaryly.R


class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<User>
    private lateinit var imagelist: Array<Int>
    private lateinit var userList: Array<String>
    private lateinit var searchView: androidx.appcompat.widget.SearchView

    val firestore = FirebaseFirestore.getInstance()
    val userCollection = firestore.collection("users")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBar: SearchView = view.findViewById(R.id.searchBar)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val userCollection = FirebaseFirestore.getInstance().collection("users")
        val userAdapter = UserAdapter(ArrayList())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = userAdapter

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchUsers(newText, userAdapter, userCollection)
                }
                return true
            }
        })
    }

    private fun searchUsers(query: String, adapter: UserAdapter, userCollection: CollectionReference) {
        userCollection
            .whereGreaterThanOrEqualTo("username", query)
            .whereLessThanOrEqualTo("username", query + "\uF7FF") // \uF7FF is a Unicode character that is higher than any regular character
            .get()
            .addOnSuccessListener { documents ->
                val users = ArrayList<UserModel>()
                for (document in documents) {
                    val user = document.toObject(UserModel::class.java)
                    users.add(user)
                }
                adapter.updateUsers(users)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}
