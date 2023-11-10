package edu.bluejack23_1.diaryly.moods

import java.io.Serializable

data class Moods(
    val id: String,
    val date: String,
    val time: String,
    val chosenMood: String,
    val notes: String
) : Serializable


