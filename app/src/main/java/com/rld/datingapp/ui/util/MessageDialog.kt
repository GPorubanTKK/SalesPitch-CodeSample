package com.rld.datingapp.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.rld.datingapp.data.Match
import com.rld.datingapp.data.Message
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.util.formatLines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.rld.datingapp.data.ViewModel.Companion.webSocketManager

@Composable fun MessageDialog(viewModel: ViewModel, match: Match, goBack: () -> Unit) = Column(
    modifier = maxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val recipient = match.user2
    val messages = viewModel.messages.observeAsState()
    val theseMessages = remember { derivedStateOf { messages.value?.get(recipient.email)!! } }
    val scope = rememberCoroutineScope()
    Row(maxWidth()) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "", Modifier.clickable(onClick = goBack))
        HorizontalSpacer(25.dp)
        Icon(recipient.profilePicture!!.asImageBitmap(), "")
        HorizontalSpacer(10.dp)
        Text(recipient.name)
    }
    LazyColumn(maxHeight(0.9).fillMaxWidth()) {
        items(theseMessages.value) { (sent, message) ->
            Row(maxWidth(), horizontalArrangement = if(sent) Arrangement.Start else Arrangement.End) {
                HorizontalSpacer(2.5.dp)
                Text(message.formatLines(50))
                HorizontalSpacer(2.5.dp)
            }
        }
    }
    Row(maxWidth(0.9)) {
        var messageToSend by rememberMutableStateOf("")
        TextField(messageToSend, { messageToSend = it }, placeholder = { Text("Message") })
        HorizontalSpacer(5.dp)
        IconButton(Icons.AutoMirrored.Filled.Send, enabled = messageToSend.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                webSocketManager.sendMessage(Message(true, messageToSend, recipient))
            }
        }
    }
}