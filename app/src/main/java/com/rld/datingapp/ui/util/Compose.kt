package com.rld.datingapp.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.maxSize(frac: Float = 1f) = fillMaxSize(frac)
fun Modifier.masWidth(frac: Float = 1f) =  fillMaxWidth(frac)
fun Modifier.maxHeight(frac: Float = 1f) = fillMaxHeight(frac)

@Composable fun VerticalSpacer(size: Dp, modifier: Modifier = Modifier) = Spacer(modifier.height(size))
@Composable fun HorizontalSpacer(size: Dp, modifier: Modifier = Modifier) = Spacer(modifier.width(size))

@Composable fun <T> rememberMutableStateOf(value: T) = remember { mutableStateOf(value) }

@Composable fun ErrorText(text: String, modifier: Modifier = Modifier) = Row(
    modifier
        .background(Color(0xAABD2626), RoundedCornerShape(10))
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