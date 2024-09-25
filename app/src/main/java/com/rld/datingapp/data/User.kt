package com.rld.datingapp.data

import android.graphics.Bitmap
import com.google.gson.annotations.Expose

data class User(
    @Expose val firstname: String,
    @Expose val lastname: String,
    @Expose val email: String,
    @Expose val phoneNumber: String,
    var profilePicture: Bitmap? = null,
    val introVideo: String? = null
) {
    val name
        get() = "$firstname $lastname"
}
