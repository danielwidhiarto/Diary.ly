package edu.bluejack23_1.diaryly.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.diaryly.R

class JournalAdapter (private val journalList:ArrayList<Journal>)
    : RecyclerView.Adapter<JournalAdapter.JournalViewHolder>(){

    class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageJournal : ImageView = itemView.findViewById(R.id.imgJournal)
        val journalTitle : TextView = itemView.findViewById(R.id.tvJournalTitle)
        val journalContent :TextView = itemView.findViewById(R.id.tvJournalContent)
        val journaldate : TextView = itemView.findViewById(R.id.tvJournalDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.journal_each_item, parent, false)
        return JournalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val journal = journalList[position]
        holder.imageJournal.setImageResource(journal.image)
        holder.journalTitle.text = journal.title
        holder.journalContent.text = journal.content
        holder.journaldate.text = journal.date
    }
}