package com.rld.datingapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.rld.datingapp.data.Match
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.ui.util.HorizontalSpacer
import com.rld.datingapp.ui.util.MessageDialog
import com.rld.datingapp.ui.util.VerticalSpacer
import com.rld.datingapp.ui.util.maxSize
import com.rld.datingapp.ui.util.maxWidth
import com.rld.datingapp.ui.util.rememberMutableStateOf

@Composable fun Messages(viewModel: ViewModel) = Column(modifier = maxSize()) {
    val (openDialog, setOpenDialog) = rememberMutableStateOf<Match?>(null)
    VerticalSpacer(40.dp)
    if(openDialog == null) {
        Row(maxWidth()) {
            Text("Messages")
        }
        VerticalSpacer(3.dp)
        LazyColumn(maxSize()) {
            items(viewModel.matches) { match ->
                val matchedUser = match.other(viewModel.loggedInUser!!)
                Row(
                    maxWidth().clickable {
                        setOpenDialog(match)
                    }
                ) {
                    Icon(Icons.Default.AccountBox, "")
                    HorizontalSpacer(10.dp)
                    Column {
                        Text(matchedUser.name)
                    }
                }
            }
        }
    } else MessageDialog(viewModel, openDialog) { setOpenDialog(null) }
}