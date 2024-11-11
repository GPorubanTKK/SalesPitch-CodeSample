package com.rld.datingapp.util

import androidx.compose.ui.util.fastRoundToInt
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlin.reflect.KProperty

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
    if(!password.matches("^(?=.*[!@#\$%^&*].*)(?=.*[a-z].*)(?=.*[0-9].*)(?=.*[A-Z].*)[A-Za-z0-9!@#\$%^&*]{8,}\$".toRegex())) {
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

/**
 * Represents a nonnull value that can only be set once. This is useful for properties that cannot be initialized at construction,
 * but for security should not be fully mutable. This will not prevent access to items if the class is used to wrap a collection.
 *
 *  @author Gedeon Poruban
 * */
class OnceSetValue<T: Any> {
    private var value: T? = null
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value!!
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        if(this.value == null) this.value = value else throw IllegalAccessException("Value has already been set")
}

enum class MimeTypes(val value: String) {
    APPLICATION_JSON("application/json"),
    APPLICATION_PLAINTEXT("text/plain;charset=UTF-8"),
    APPLICATION_BINARY("application/octet-stream"),
    APPLICATION_HTML("text/html"),
    MEDIA_MP3("audio/mpeg"),
    MEDIA_MP4("video/mp4"),
    MEDIA_JPEG("image/jpeg"),
    MEDIA_PNG("image/png")
}

const val HTTP_OK = 200

const val SERVER_ADDR = "http://10.0.2.2:8080/app/api"
//const val SERVER_ADDR = "http://10.23.102.199:8080/app/api"

infix fun <A, B, C>  Pair<A, B>.too(other: C): Triple<A, B, C> = Triple(first, second, other)