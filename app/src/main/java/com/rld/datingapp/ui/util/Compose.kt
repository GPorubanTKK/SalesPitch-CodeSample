package com.rld.datingapp.ui.util

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rld.datingapp.data.User

@SuppressLint("ModifierFactoryExtensionFunction")
fun maxSize(frac: Double = 1.0) = Modifier.fillMaxSize(frac.toFloat())
@SuppressLint("ModifierFactoryExtensionFunction")
fun maxWidth(frac: Double = 1.0) = Modifier.fillMaxWidth(frac.toFloat())
@SuppressLint("ModifierFactoryExtensionFunction")
fun maxHeight(frac: Double = 1.0) = Modifier.fillMaxHeight(frac.toFloat())

@Composable fun VerticalSpacer(size: Dp, modifier: Modifier = Modifier) = Spacer(modifier.height(size))
@Composable fun HorizontalSpacer(size: Dp, modifier: Modifier = Modifier) = Spacer(modifier.width(size))

@Composable fun <T> rememberMutableStateOf(value: T) = remember { mutableStateOf(value) }

@Composable fun ErrorText(text: String, modifier: Modifier = Modifier) = Row(
    modifier
        .background(Color(0xAABD2626))
        .padding(5.dp, 2.5.dp)
) { Text(text) }

@Composable fun NavIcon(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) = Column(
    modifier = Modifier.clickable(enabled = !isActive, onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Icon(icon, "")
    VerticalSpacer(1.dp)
    Text(label)
}

@Composable fun ProfileCard(user: User?, modifier: Modifier = Modifier) = Column(
    modifier
        .fillMaxSize()
        .background(Color(0xFF9EEFEF), RoundedCornerShape(1.dp)),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    if(user == null) Text("No more matches right now") else {
        Icon(user.profilePicture!!.asImageBitmap(), "", modifier = maxSize(0.8))
        VerticalSpacer(25.dp)
        Text("${user.firstname} ${user.lastname}")
    }
}