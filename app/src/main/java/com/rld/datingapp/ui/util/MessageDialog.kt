package com.rld.datingapp.ui.util

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rld.datingapp.LOGGERTAG
import com.rld.datingapp.data.Match
import com.rld.datingapp.data.Message
import com.rld.datingapp.data.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable fun MessageDialog(viewModel: ViewModel, match: Match, goBack: () -> Unit) = Column(
    modifier = maxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val recipient = match.other(viewModel.loggedInUser!!)
    val scope = rememberCoroutineScope()
    Row(maxWidth()) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "", Modifier.clickable(onClick = goBack))
        HorizontalSpacer(25.dp)
        Icon(Icons.Default.AccountBox, "")
        HorizontalSpacer(10.dp)
        Text(recipient.name)
    }
    key(viewModel.messageUpdateCounter) {
        LazyColumn(maxHeight(0.8).fillMaxWidth()) {
            items(viewModel.messages[recipient.email]!!) { (sent, message) ->
                Row(
                    maxWidth()
                        .padding(horizontal = 2.dp, vertical = 1.dp)
                        .background(if (sent) Color.LightGray else Color.Cyan),
                    horizontalArrangement = if (sent) Arrangement.Start else Arrangement.End
                ) {
                    HorizontalSpacer(2.5.dp)
                    Text(message.formatLines(50))
                    HorizontalSpacer(2.5.dp)
                }
            }
        }
    }
    Row(maxWidth(0.9)) {
        var messageToSend by rememberMutableStateOf("")
        TextField(messageToSend, { messageToSend = it }, placeholder = { Text("Message") })
        HorizontalSpacer(5.dp)
        IconButton(Icons.AutoMirrored.Filled.Send, enabled = messageToSend.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                val msg = Message(true, messageToSend, recipient)
                Log.d(LOGGERTAG, "Sending $msg")
                messageToSend = ""
                viewModel.addMessage(recipient.email, msg)
            }
        }
    }
    VerticalSpacer(70.dp)
}