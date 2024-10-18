package com.rld.datingapp.data

import com.google.gson.annotations.Expose
import com.rld.datingapp.util.exposeAwareGson

data class Message(
    @Expose val clientIsSender: Boolean,
    @Expose val value: String,
    @Expose val recipient: User? = null
): Serializable {
    override fun serialize(): String = exposeAwareGson().toJson(this, Message::class.java)
    companion object: Deserializable<Message> {
        override fun deserialize(serialized: String): Message = exposeAwareGson().fromJson(serialized, Message::class.java)
    }
}