package com.rld.datingapp.data

import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.rld.datingapp.util.exposeAwareGson

data class User(
    @Expose val firstName: String,
    @Expose val lastName: String,
    @Expose val email: String,
    @Expose val phoneNumber: String,
    var profilePicture: Bitmap? = null,
): Serializable {
    val name get() = "$firstName $lastName"
    override fun serialize(): String = exposeAwareGson().toJson(this, User::class.java)
    companion object: Deserializable<User> {
        override fun deserialize(serialized: String): User = exposeAwareGson().fromJson(serialized, User::class.java)
    }
}
