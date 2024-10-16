package com.rld.datingapp.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.ui.util.ErrorText
import com.rld.datingapp.ui.util.HorizontalSpacer
import com.rld.datingapp.ui.util.VerticalSpacer
import com.rld.datingapp.ui.util.maxSize
import com.rld.datingapp.ui.util.maxWidth

@Composable fun Settings(viewModel: ViewModel) = Column(modifier = maxSize().padding(10.dp)) {
    VerticalSpacer(40.dp)
    Row(maxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            viewModel.loggedInUser!!.profilePicture?.asImageBitmap()!!,
            "",
            modifier = Modifier.size(50.dp, 50.dp).border(0.dp, Color(0x00000000), CircleShape)
        )
        HorizontalSpacer(15.dp)
        Text("Hi, ${viewModel.loggedInUser!!.name}!")
    }
    VerticalSpacer(50.dp)
    ErrorText("This feature (Settings) will be implemented at a later time.  Thank you for your patience.")
}