package edu.bluejack23_1.diaryly.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import edu.bluejack23_1.diaryly.R

class UserAdapter(private val userList: ArrayList<User>)
    : RecyclerView.Adapter<UserAdapter.userViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_each_item, parent, false)
        return userViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
        return userList.size
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        TODO("Not yet implemented")
//        val currentItem = userList[position]
//        holder.userprofile.setImageResource(currentItem.image)
//        holder.username.text = currentItem.title

    }


    class userViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userprofile:ImageView = itemView.findViewById(R.id.profile_image)
        val username : TextView = itemView.findViewById(R.id.etUsername)
    }

}

