package edu.bluejack23_1.diaryly.journal

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.bluejack23_1.diaryly.R

class JournalAdapter(private val journalList: ArrayList<Journal>) :
    RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {

    class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageJournal: ImageView = itemView.findViewById(R.id.imgJournal)
        val journalTitle: TextView = itemView.findViewById(R.id.tvJournalTitle)
        val journalContent: TextView = itemView.findViewById(R.id.tvJournalContent)
        val journaldate: TextView = itemView.findViewById(R.id.tvJournalDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.journal_each_item, parent, false)
        return JournalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val journal = journalList[position]
        holder.journalTitle.text = journal.title
        holder.journalContent.text = journal.content
        holder.journaldate.text = journal.date

        // Load and display the user's profile image using Picasso
        if (!journal.image.isNullOrEmpty()) {
            Picasso.get().load(journal.image).into(holder.imageJournal)
        } else {
            // Handle the case where the profile image URL is not available or empty
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, JournalDetailActivity::class.java)
            intent.putExtra(
                "journalsId", journal.id
            ) // Assuming journal.id is a String or an identifier
            context.startActivity(intent)
        }
    }
}