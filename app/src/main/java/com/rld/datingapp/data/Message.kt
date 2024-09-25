package com.rld.datingapp.data

import com.google.gson.annotations.Expose

data class Message(
    @Expose val clientIsSender: Boolean,
    @Expose val value: String,
    @Expose val recipient: User? = null
)