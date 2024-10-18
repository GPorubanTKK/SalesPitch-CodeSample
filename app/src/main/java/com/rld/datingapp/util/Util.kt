package com.rld.datingapp.util

import android.graphics.Bitmap
import androidx.compose.ui.util.fastRoundToInt
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

fun Bitmap.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 90, baos)
    return baos.toByteArray()
}

fun exposeAwareGson(): Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

fun checkSignupParams(
    firstName: String,
    lastName: String,
    password: String,
    phoneNumber: String,
    email: String
): Pair<Boolean, String> {
    var hasError = firstName.isBlank() || lastName.isBlank()
    val errorText = mutableListOf<String>()
    if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$".toRegex())) {
        hasError = true
        errorText += "Password must contain a number, a special character, and a capital letter."
    }
    if(!phoneNumber.matches("^\\d{3}-\\d{3}-\\d{4}$".toRegex())) {
        hasError = true
        errorText += "Please enter a valid phone number."
    }
    if(!email.matches("^[a-z0-9]+@[a-z]+\\.[a-z]{2,3}\$".toRegex())) {
        hasError = true
        errorText += "Please enter a valid email address."
    }
    return hasError to errorText.joinToString("\n-", prefix = "-")
}

fun String.formatLines(lengthOfLine: Int = 40): String {
    val wordsIterator = split("[ \n\t]+".toRegex()).iterator()
    val lines = mutableListOf<String>()
    while(wordsIterator.hasNext()) {
        var line = ""
        try {
            while(line.length < (lengthOfLine * 0.9).fastRoundToInt() ) line += "${wordsIterator.next()} "
        } catch (ignored: NoSuchElementException) {}
        lines += line
    }
    return lines.joinToString("\n") { it.trim() }.trim()
}

