package com.rld.datingapp.messaging

import android.util.Log
import com.google.gson.JsonObject
import com.rld.datingapp.LOGGERTAG
import com.rld.datingapp.data.Message
import com.rld.datingapp.data.ViewModel
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager(
    private val viewModel: ViewModel
): WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        println("Established connection with code ${response.code}")
        viewModel.setConnectionStatus(response.code == 200)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val (messageType, _, from, payload) = WebSocketMessage.from(text)
        when(messageType!!) {
            MessageType.Identify -> throw IllegalStateException("Client should not receive id messages from server")
            MessageType.Message -> {
                viewModel.addMessage(from!!, Message(false, payload!!["content"].asString))
            }
            MessageType.Match -> TODO("Implement match handling with websockets")
            MessageType.System -> {
                Log.d(LOGGERTAG, "Received system message from server: $payload")
            }
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        println("Closed connection.  Code: $code Reason: $reason")
        viewModel.setConnectionStatus(false)
    }

    fun authorizeConnection(id: String, passkey: String): Boolean {
        try {
            ViewModel.webSocket.send(IdentityMessage(id, JsonObject().apply { addProperty("password", passkey) }))
        } catch (ignored: Exception) { return false }
        return true
    }
}