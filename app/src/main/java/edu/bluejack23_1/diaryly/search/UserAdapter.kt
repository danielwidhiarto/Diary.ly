package edu.bluejack23_1.diaryly.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.diaryly.R
class UserAdapter(private val userList: ArrayList<UserModel>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // Add a public method to update the user list
    fun updateUsers(users: List<UserModel>) {
        userList.clear()
        userList.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_each_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.username.text = user.username
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.tvUsername)
    }
}

