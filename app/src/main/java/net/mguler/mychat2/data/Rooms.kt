package net.mguler.mychat2.data

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Rooms(
    val roomName: String? = null,
    val roomLevel: String? = null,
    val roomMessages: String? = null,

// TODO: room messages gerekli mi
// tek logout var çoklu olmalı

)
