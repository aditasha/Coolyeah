package com.papb.coolyeah

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User_Model(
    var uid: String? = "",
    var email: String? = "",
    var username: String? = "",
    var image: String? = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "username" to username,
            "image" to image
        )
    }
}