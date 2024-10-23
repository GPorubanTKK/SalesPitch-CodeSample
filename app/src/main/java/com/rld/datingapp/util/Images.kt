package com.rld.datingapp.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import java.io.ByteArrayOutputStream

fun Image.toBitmap(): Bitmap {
    val buf = planes[0].buffer
    val bytes = ByteArray(buf.capacity())
    buf.get(bytes); buf.clear()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 90, stream)
    return stream.toByteArray().also { recycle() }
}

fun ByteArray.toBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)