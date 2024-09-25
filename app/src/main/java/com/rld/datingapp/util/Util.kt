package com.rld.datingapp.util

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import com.rld.datingapp.CHANNEL_ID
import java.io.ByteArrayOutputStream

fun Context.makeSmallNotification(
    icon: Int,
    title: String,
    content: String,
    priority: Int = NotificationCompat.PRIORITY_HIGH
) {
    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(icon)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(priority)
        .build()
    val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(0, notification)
}

fun String.formatLines(length: Int = 40): String {
    val out = mutableListOf<Char>()
    toList().chunked(length).forEach { out += it; out += '\n' }
    return out.joinToString { it.toString() }
}

fun Bitmap.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 90, baos)
    return baos.toByteArray()
}