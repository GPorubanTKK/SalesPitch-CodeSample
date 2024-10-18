package com.rld.datingapp.messaging

import android.util.Log
import com.google.gson.JsonObject
import com.rld.datingapp.LOGGERTAG
import com.rld.datingapp.data.Match
import com.rld.datingapp.data.Message
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.data.ViewModel.Companion.controller
import com.rld.datingapp.data.ViewModel.Companion.okHttpClient
import com.rld.datingapp.data.ViewModel.Companion.webSocketManager
import com.rld.datingapp.util.exposeAwareGson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager(
    private val viewModel: ViewModel
): WebSocketListener() {
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
        when(messageType!!) {
            MessageType.Identify -> throw IllegalStateException("Client should not receive id messages from server")
            MessageType.Message -> viewModel.messages[from!!]!!.add(Message(false, payload!!["content"].asString))
            MessageType.Match -> {
                viewModel.matches += exposeAwareGson().fromJson(payload!!["content"].asString, Match::class.java)
                viewModel.addRecipient(from!!)
                //sendNotification(payload!!["message"].asString)
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
            Log.d(LOGGERTAG,"WS: ${ViewModel.webSocket}")
            ViewModel.webSocket.send(IdentityMessage(id, JsonObject().apply { addProperty("password", passkey) }))
            return true
        } catch (ignored: Exception) { return false }
    }

    private fun WebSocket.send(message: WebSocketMessage) = send(exposeAwareGson().toJson(message, message::class.java))
}