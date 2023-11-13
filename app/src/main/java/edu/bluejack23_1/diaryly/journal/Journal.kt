package edu.bluejack23_1.diaryly.journal
import java.io.Serializable

data class Journal(
    val id: String,
    val title: String,
    val date: String,
    val content: String,
    val image_url: String,
    val visibility: String

) : Serializable {
}
