package com.rld.datingapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.rld.datingapp.ApiController
import com.rld.datingapp.messaging.WebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

@OptIn(SavedStateHandleSaveableApi::class)
class ViewModel(savedState: SavedStateHandle): ViewModel() {
    init {
        controller = ApiController(this)
        webSocketManager = WebSocketManager(this)
        viewModelScope.launch(Dispatchers.IO) {
            webSocket = okHttpClient.newWebSocket(
                Request.Builder()
                    .url("ws://10.0.2.2:8080/messages")
                    .build(),
                webSocketManager
            )
        }
    }

    var websocketConnectionState: Boolean by savedState.saveable { mutableStateOf(false) }
    var loggedInUser: User? by savedState.saveable { mutableStateOf<User?>(null) }
    val matches: MutableList<Match> by savedState.saveable(
        saver = listSaver(
            save = { it.map(Match::serialize) },
            restore = { it.map(Match::deserialize).toMutableStateList() }
        )
    ) { mutableStateListOf() }
    private val pMessages: MutableMap<String, SnapshotStateList<Message>> by mutableStateMapOf()
    val messages: Map<String, SnapshotStateList<Message>> = pMessages
    var messageUpdateCounter: Int by mutableIntStateOf(0)

    fun verifySocketConnection(password: String): Boolean = webSocketManager.authorizeConnection(loggedInUser!!.email, password)
    fun addMessage(email: String, msg: Message) {
        pMessages[email]!!.add(msg)
        messageUpdateCounter++
    }
    fun addRecipient(email: String) {
        pMessages[email] = mutableStateListOf()
        messageUpdateCounter++
    }

    companion object {
        lateinit var webSocketManager: WebSocketManager
        lateinit var webSocket: WebSocket
        lateinit var controller: ApiController
        val okHttpClient = OkHttpClient()
    }
}