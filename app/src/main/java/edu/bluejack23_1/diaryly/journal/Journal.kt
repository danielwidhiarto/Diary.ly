package edu.bluejack23_1.diaryly.journal

import java.io.Serializable

data class Journal(
    val id: String,
    val title: String,
    val date: String,
    val content: String,
    val image: String,
    val visibility: String,
    val userId: String = "" // Add this line

) : Serializable {

    constructor() : this("", "", "", "", "", "", "")
}
