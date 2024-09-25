package com.rld.datingapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.rld.datingapp.data.ViewModel.Companion.controller
import com.rld.datingapp.data.Match
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.ui.util.HorizontalSpacer
import com.rld.datingapp.ui.util.MessageDialog
import com.rld.datingapp.ui.util.VerticalSpacer
import com.rld.datingapp.ui.util.maxSize
import com.rld.datingapp.ui.util.maxWidth
import com.rld.datingapp.ui.util.rememberMutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable fun Messages(viewModel: ViewModel) = Column(modifier = maxSize()) {
    var error by rememberMutableStateOf<String?>(null)
    val (openDialog, setOpenDialog) = rememberMutableStateOf<Match?>(null)
    val user = viewModel.user.observeAsState()
    val matches = viewModel.matches.observeAsState()
    if(error != null) Text(error!!) else {
        if(openDialog == null) {
            Row(maxWidth()) {
                Text("Messages")
            }
            VerticalSpacer(3.dp)
            LazyColumn(maxSize()) {
                items(matches.value!!) { match ->
                    val (_, matchedUser) = match
                    Row(
                        maxWidth().clickable {
                            setOpenDialog(match)
                        }
                    ) {
                        Icon(matchedUser.profilePicture!!.asImageBitmap(), "")
                        HorizontalSpacer(10.dp)
                        Column {
                            Text(matchedUser.lastname)
                            //Text("$minsLeft minutes remaining")
                        }
                    }
                }
            }
        } else MessageDialog(viewModel, openDialog) { setOpenDialog(null) }
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            matches.value?.clear()
            val results = controller.getMatches(user.value!!)
            for(result in results) viewModel.addMatch(result)
        }
    }
}