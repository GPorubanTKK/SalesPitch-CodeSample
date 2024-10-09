package com.rld.datingapp.messaging

import com.google.gson.GsonBuilder
import okhttp3.WebSocket

internal fun WebSocket.send(message: WebSocketMessage) {
    val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    send(gson.toJson(message, message::class.java))
}