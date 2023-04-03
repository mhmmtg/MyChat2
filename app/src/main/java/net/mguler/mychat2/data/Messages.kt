package net.mguler.mychat2.data

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Messages(
    val userId: String? = null,
    val messageBody: String? = null,
    val messageTime: Long? = null
)
