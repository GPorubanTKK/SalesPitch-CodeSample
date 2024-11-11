package com.rld.datingapp.messaging

import android.util.Log
import com.google.gson.JsonObject
import com.rld.datingapp.LOGGERTAG
import com.rld.datingapp.data.Match
import com.rld.datingapp.data.Message
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.data.ViewModel.Companion.okHttpClient
import com.rld.datingapp.data.ViewModel.Companion.webSocket
import com.rld.datingapp.data.ViewModel.Companion.webSocketManager
import com.rld.datingapp.util.exposeAwareGson
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager(private val viewModel: ViewModel): WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d(LOGGERTAG, "Established connection with code ${response.code}")
        val statusCodeOk = response.code == 101
        viewModel.websocketConnectionState = statusCodeOk
        if(!statusCodeOk) {
            ViewModel.webSocket = okHttpClient.newWebSocket(
                Request.Builder()
                    .url("ws://10.0.2.2:8080/messages")
                    .build(),
                webSocketManager
            )
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val (messageType, _, from, payload) = WebSocketMessage.from(text)
        val gson = exposeAwareGson()
        when(messageType!!) {
            MessageType.Identify -> {}
            MessageType.Message -> viewModel.addMessage(from!!, Message(false, payload!!["content"].asString))
            MessageType.Match -> {
                viewModel.matches += gson.fromJson(payload!!["content"].asString, Match::class.java)
                viewModel.addRecipient(from!!)
            }
            MessageType.System -> {
                Log.d(LOGGERTAG, "Received System message: ${payload!!["content"].asString}")
            }
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d(LOGGERTAG, "Closed connection.  Code: $code Reason: $reason")
        viewModel.websocketConnectionState = false
    }

    fun authorizeConnection(id: String, passkey: String): Boolean {
        try {
            Log.d(LOGGERTAG,"WS: $webSocket")
            webSocket.send(IdentityMessage(id, JsonObject().apply { addProperty("password", passkey) }))
            return true
        } catch (ignored: Exception) { return false }
    }

    fun sendMessage(message: Message) {
        val wsm = DirectMessage(
            from = viewModel.loggedInUser!!.email,
            to = message.recipient!!.email,
            payload = JsonObject().apply { addProperty("content", message.value) }
        )
        webSocket.send(wsm)
    }

    private fun WebSocket.send(message: WebSocketMessage) = send(exposeAwareGson().toJson(message, message::class.java))
}