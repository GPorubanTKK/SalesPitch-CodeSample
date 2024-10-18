package com.rld.datingapp.ui

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rld.datingapp.LOGGERTAG
import com.rld.datingapp.NavPosition
import com.rld.datingapp.NavPosition.Login
import com.rld.datingapp.data.ViewModel.Companion.controller
import com.rld.datingapp.ui.util.*
import com.rld.datingapp.util.checkSignupParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable fun Signup(setNavState: (NavPosition) -> Unit, sharedPreference: SharedPreferences) {
    Column(maxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val scope = rememberCoroutineScope()
        var firstNameText by rememberMutableStateOf("")
        var lastNameText by rememberMutableStateOf("")
        var emailText by rememberMutableStateOf("")
        var passwordText by rememberMutableStateOf("")
        var errorMessage by rememberMutableStateOf("")
        var showError by rememberMutableStateOf(false)
        var phoneNumberText by rememberMutableStateOf("")
        var firstStage by rememberMutableStateOf(true)
        VerticalSpacer(40.dp)
        Row(maxWidth(), horizontalArrangement = Arrangement.Start) {
            HorizontalSpacer(15.dp)
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "", modifier = Modifier.clickable {
                if(firstStage) setNavState(Login) else firstStage = true
            })
        }
        VerticalSpacer(15.dp)
        if(firstStage) {
            LabeledTextField(
                { Text("First name:") },
                firstNameText,
                { firstNameText = it },
                placeHolder = "First name"
            )
            VerticalSpacer(20.dp)
            LabeledTextField(
                { Text("Last name:") },
                lastNameText,
                { lastNameText = it },
                placeHolder = "Last name"
            )
            VerticalSpacer(20.dp)
            LabeledTextField(
                { Text("Email address:") },
                emailText,
                { emailText = it },
                placeHolder = "Email address"
            )
            VerticalSpacer(20.dp)
            LabeledTextField(
                { Text("Password") },
                passwordText,
                { passwordText = it },
                placeHolder = "Password"
            )
            VerticalSpacer(20.dp)
            Column {
                Text("Phone number:")
                TextField(
                    phoneNumberText,
                    { if ((it.matches("[\\d\\-]+".toRegex()) || it.isEmpty()) && it.length <= 12) phoneNumberText = it },
                    placeholder = { Text("XXX-XXX-XXXX") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
            VerticalSpacer(20.dp)
            TextButton("Next") {
                if (!phoneNumberText.matches("\\d{3}-\\d{3}-\\d{4}".toRegex())) {//phone number isn't xxx-xxx-xxxx
                    val filteredString = phoneNumberText.filter { it.isDigit() } //filter to xxxxxxxxxx
                    phoneNumberText =
                        "${filteredString.substring(0..2)}-${filteredString.substring(3..5)}-${filteredString.substring(6..9)}"
                }
                val (show, error) = checkSignupParams(
                    firstNameText,
                    lastNameText,
                    passwordText,
                    phoneNumberText,
                    emailText
                )
                errorMessage = error
                showError = show
                firstStage = show
            }
        } else {
            var hasPermission by rememberMutableStateOf(0)
            LaunchedEffect(Unit) {
                if(hasPermission == 0) {
                    val hasPermissions = sharedPreference.getBoolean("hasCameraPref", false)
                    hasPermission = if(hasPermissions) 1 else 2
                }
            }
            when(hasPermission) {
                0 -> {}
                1 -> {
                    var imageCapture by rememberMutableStateOf<Bitmap?>(null)
                    var imageIsOkay by rememberMutableStateOf(false)
                    var imageSelected by rememberMutableStateOf(false)
                    if(imageCapture == null)
                        CameraPreviewScreen(
                            modifier = maxWidth().fillMaxHeight(0.8f),
                            onImageCaptured = { value: ImageProxy -> imageCapture = value.toBitmap() })
                        { faces ->
                            imageIsOkay = faces.isNotEmpty()
                            if(!imageIsOkay) Log.d(LOGGERTAG, "INVALID IMAGE, NO FACES")
                        }
                    else {
                        Image(imageCapture!!.asImageBitmap(), "", maxWidth().fillMaxHeight(0.8f))
                        if(!imageSelected) {
                            VerticalSpacer(5.dp)
                            Row {
                                IconButton(Icons.Filled.Check, enabled = imageIsOkay) { imageSelected = true }
                                HorizontalSpacer(30.dp)
                                IconButton(Icons.Filled.Clear) { imageIsOkay = false; imageCapture = null }
                            }
                        }
                    }
                    VerticalSpacer(15.dp)
                    TextButton("Signup", enabled = imageSelected) {
                        scope.launch(Dispatchers.IO) {
                            val result = controller.attemptMakeAccount(
                                firstNameText,
                                lastNameText,
                                passwordText,
                                emailText,
                                phoneNumberText,
                                imageCapture!!
                            )
                            if (result) setNavState(Login) else {
                                errorMessage = "Something went wrong creating your account.  Try again later."
                                showError = true
                            }
                        }
                    }
                }
                2 -> Text("Please grant permissions to continue.")
            }
        }
        VerticalSpacer(10.dp)
        if(showError) ErrorText(errorMessage)
    }
}