package com.rld.datingapp.messaging

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import com.rld.datingapp.LOGGERTAG
import com.rld.datingapp.R
import com.rld.datingapp.data.Match
import com.rld.datingapp.data.Message
import com.rld.datingapp.data.User
import com.rld.datingapp.data.ViewModel
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager(
    private val viewModel: ViewModel
): WebSocketListener() {
    private val gson = Gson()
        .newBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        viewModel.setConnectionStatus(true)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val json = JsonParser.parseString(text).asJsonObject
        try {
            when(MessageType.valueOf(json["type"].asString)) { //make sure the first letter is capital to work with enum
                MessageType.Match -> {
                    val message = gson.fromJson(json, WSMatch::class.java)
                    val match = message.value
                    val from = message.from
                    //update matches from db
                    viewModel.sendNotification(R.drawable.baseline_notifications_24, "You matched with ${from?.name}! Your have 24h to sell yourself.", "")
                }
                MessageType.Message -> {
                    val message = gson.fromJson(json, WSMessage::class.java)
                    val sender = message.from
                    viewModel.addMessage(sender!!.email, message.value!!)
                    viewModel.sendNotification(R.drawable.baseline_notifications_24, "${sender.name} sent you a message!", message.value.value)
                }
                MessageType.System -> {
                    Log.d(LOGGERTAG, "Got $text")
                }
                MessageType.None -> {}
            }
        } catch(ignored: Exception) {}
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        viewModel.setConnectionStatus(false)
    }

    suspend fun sendMessage(message: Message) = ViewModel.webSocket.send(
        makeMessage(WSMessage(message))
    )

    enum class MessageType {
        Match,
        Message,
        System,
        None
    }

    abstract class WebSocketTransmission<T>(@Expose open val value: T? = null) {
        @Expose open val type: MessageType = MessageType.None
        @Expose val from: User? = null
    }

    class WSMessage(message: Message) : WebSocketTransmission<Message>(message) {
        override val type: MessageType = MessageType.Message
    }

    class WSMatch(match: Match) : WebSocketTransmission<Match>(match) {
        override val type: MessageType = MessageType.Match
    }

    private fun makeMessage(message: WSMessage): String {
        val encoder = Gson()
            .newBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
        return encoder.toJson(message, WSMessage::class.java)
    }
}