package edu.bluejack23_1.diaryly.moods

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.diaryly.R

class MoodsAdapter(var moodList: ArrayList<Moods>) : RecyclerView.Adapter<MoodsAdapter.MoodsViewHolder>() {

    class MoodsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editMoods: TextView = itemView.findViewById(R.id.tvEdit)
        val moodDate: TextView = itemView.findViewById(R.id.moodsDate)
        val moodTime: TextView = itemView.findViewById(R.id.moodsTime)
        val moodDescription: TextView = itemView.findViewById(R.id.moods)
        val moodNotes: TextView = itemView.findViewById(R.id.moodsNotes)
        val moodEmoji: ImageView = itemView.findViewById(R.id.imgEmoji) // Add this line
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.moods_each_item, parent, false)
        return MoodsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return moodList.size
    }

    override fun onBindViewHolder(holder: MoodsViewHolder, position: Int) {
        val mood = moodList[position]
        holder.moodDate.text = mood.date
        holder.moodTime.text = mood.time
        holder.moodDescription.text = mood.chosenMood
        holder.moodNotes.text = mood.notes

        // Set the emoji based on the chosenMood
        holder.moodEmoji.setImageResource(getEmojiResource(mood.chosenMood))

        holder.editMoods.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditMoodsActivity::class.java)
            intent.putExtra("MOOD_ID", mood.id) // Pass the mood ID to EditActivity
            context.startActivity(intent)
        }


    }

    private fun getEmojiResource(mood: String): Int {
        return when (mood) {
            "Very Happy" -> R.drawable.emoji_veryhappy
            "Happy" -> R.drawable.emoji_happy
            "Neutral" -> R.drawable.emoji_neutral
            "Sad" -> R.drawable.emoji_sad
            "Very Sad" -> R.drawable.emoji_verysad
            else -> R.drawable.ic_launcher_foreground // Use a default emoji for unknown moods
        }
    }

    fun updateData(newMoodList: ArrayList<Moods>) {
        moodList = newMoodList
        notifyDataSetChanged()
    }

}
