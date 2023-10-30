package edu.bluejack23_1.diaryly.search

data class UserModel(
    val username: String,
    val userId: String,  // Add this property if it exists in your Firestore document
    val email: String    // Add this property if it exists in your Firestore document
) {
    constructor() : this("", "", "")
}
