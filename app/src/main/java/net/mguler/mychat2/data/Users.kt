package net.mguler.mychat2.data

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Users(
    val username: String? = null,
    val gender: String? = null,
    val city: String? = null,
    val age: String? = null,
    val photo: String? = null,
    val level: Int? = null)
