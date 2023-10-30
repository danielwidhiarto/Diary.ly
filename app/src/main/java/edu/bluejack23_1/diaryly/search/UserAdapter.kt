package edu.bluejack23_1.diaryly.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.bluejack23_1.diaryly.R

class UserAdapter(private val userList: ArrayList<UserModel>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // Add a public method to update the user list
    fun updateUsers(users: List<UserModel>) {
        userList.clear()
        userList.addAll(users)
        notifyDataSetChanged()
    }


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.tvUsername)
        val profileImage : ImageView = itemView.findViewById(R.id.img_profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.search_each_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.username.text = user.username

        // Load and display the user's profile image using Picasso
        if (!user.image_url.isNullOrEmpty()) {
            Picasso.get().load(user.image_url).into(holder.profileImage)
        } else {
            // Handle the case where the profile image URL is not available or empty
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailSearchActivity::class.java)

            // Pass user data as an extra
            intent.putExtra("user", user)

            context.startActivity(intent)
        }

    }
}

