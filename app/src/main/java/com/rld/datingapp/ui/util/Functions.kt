package com.rld.datingapp.ui.util

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