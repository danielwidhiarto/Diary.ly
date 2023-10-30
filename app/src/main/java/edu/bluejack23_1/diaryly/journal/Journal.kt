package edu.bluejack23_1.diaryly.journal
import java.io.Serializable

data class Journal(
    val title: String,
    val date: String,
    val content: String,
    val image: Int,
    val visibility: String
) : Serializable
