package com.rld.datingapp.data

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rld.datingapp.ApiController
import com.rld.datingapp.LOGGERTAG
import com.rld.datingapp.messaging.WebSocketManager
import com.rld.datingapp.util.makeSmallNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import com.rld.datingapp.MainActivity.Companion.CONTEXT as context

class ViewModel: ViewModel() {
    init {
        webSocketManager = WebSocketManager(this)
        controller = ApiController(this)
        viewModelScope.launch(Dispatchers.IO) {
            webSocket = okHttpClient.newWebSocket(
                Request.Builder()
                    .url("ws://10.0.2.2:8080/messages")
                    .build(),
                webSocketManager
            )
        }
    }

    private val pConnectionStatus = MutableLiveData(false)
    val connectionStatus: LiveData<Boolean> = pConnectionStatus
    fun setConnectionStatus(status: Boolean) = viewModelScope.launch {
        pConnectionStatus.value = status
    }

    private val pUser = MutableLiveData<User>()
    val user: LiveData<User> = pUser
    fun setUser(user: User) = viewModelScope.launch {
        pUser.value = user
    }

    private val pMessages = MutableLiveData<MutableMap<String, MutableList<Message>>>(mutableMapOf())
    val messages: LiveData<MutableMap<String, MutableList<Message>>> = pMessages
    fun addMessage(from: String, message: Message) = viewModelScope.launch {
        pMessages.value!![from]!!.add(message) //update the list
        pMessages.value = pMessages.value //trigger the observer
    }
    fun removeRecipient(person: String) = viewModelScope.launch {
        pMessages.value?.remove(person)
        pMessages.value = pMessages.value
    }
    private val pMatches = MutableLiveData<MutableList<Match>>(mutableListOf())
    val matches: LiveData<MutableList<Match>> = pMatches
    fun addMatch(user: Match, reference: User) = viewModelScope.launch {
        pMatches.value?.add(user)
        val key = if(reference.email == user.user1.email) user.user2.email else user.user1.email
        if(!pMessages.value!!.keys.contains(key))
            pMessages.value!![key] = mutableListOf()
        pMatches.value = pMatches.value
        pMessages.value = pMessages.value
        Log.d(LOGGERTAG, "Added match and message reference. ${pMessages.value}")
    }

    fun sendNotification(
        icon: Int,
        title: String,
        content: String,
        priority: Int = NotificationCompat.PRIORITY_HIGH
    ) = context.makeSmallNotification(icon, title, content, priority)

    fun verifySocketConnection(password: String): Boolean = webSocketManager.authorizeConnection(password)

    companion object {
        lateinit var webSocketManager: WebSocketManager
        lateinit var webSocket: WebSocket
        lateinit var controller: ApiController
        val okHttpClient = OkHttpClient()
    }
}