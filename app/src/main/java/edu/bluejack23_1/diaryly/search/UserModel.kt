package edu.bluejack23_1.diaryly.search

import java.io.Serializable

data class UserModel(
    val username: String,
    val userId: String,
    val email: String,
    val image_url: String // Add the image URL field here
) : Serializable {
    constructor() : this("", "", "", "")
}
