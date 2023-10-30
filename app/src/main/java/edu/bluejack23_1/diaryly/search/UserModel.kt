package edu.bluejack23_1.diaryly.search

import java.io.Serializable

data class UserModel(
    val username: String,
    val userId: String,
    val email: String
) : Serializable {
    constructor() : this("", "", "")
}
