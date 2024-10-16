package com.rld.datingapp.messaging

import com.rld.datingapp.util.exposeAwareGson
import okhttp3.WebSocket

internal fun WebSocket.send(message: WebSocketMessage) = send(exposeAwareGson().toJson(message, message::class.java))