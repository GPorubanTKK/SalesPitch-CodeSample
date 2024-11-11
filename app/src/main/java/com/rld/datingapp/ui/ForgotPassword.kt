package com.rld.datingapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rld.datingapp.NavPosition
import com.rld.datingapp.NavPosition.Login
import com.rld.datingapp.data.ViewModel.Companion.controller
import com.rld.datingapp.ui.components.LabeledTextField
import com.rld.datingapp.ui.components.TextButton
import com.rld.datingapp.ui.util.ErrorText
import com.rld.datingapp.ui.util.HorizontalSpacer
import com.rld.datingapp.ui.util.VerticalSpacer
import com.rld.datingapp.ui.util.maxSize
import com.rld.datingapp.ui.util.maxWidth
import com.rld.datingapp.ui.util.rememberMutableStateOf
import kotlinx.coroutines.launch

@Composable fun ForgotPassword(setNavState: (NavPosition) -> Unit) {
    val scope = rememberCoroutineScope()
    var usernameText by rememberMutableStateOf("")
    var showPopup by rememberMutableStateOf(false)
    var code by rememberMutableStateOf("")
    var codeStatus by rememberMutableStateOf(0)

    Column(Modifier.maxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        VerticalSpacer(40.dp)
        Row(Modifier.maxWidth(), horizontalArrangement = Arrangement.Start) {
            HorizontalSpacer(15.dp)
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "", modifier = Modifier.clickable {
                setNavState(Login)
            })
        }
        VerticalSpacer(15.dp)
        Row(Modifier.maxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
            LabeledTextField({ Text("Username") }, usernameText, { usernameText = it })
            HorizontalSpacer(20.dp)
            TextButton("Go!") {
                scope.launch {
                    val result = controller.sendPasswordReset(usernameText)
                    showPopup = result
                }
            }
        }
        if(showPopup) {
            Text("We've sent a code to the email associated with the account $usernameText.\nThis code will expire in 30 minutes")
            VerticalSpacer(30.dp)
            Row(Modifier.maxWidth(), horizontalArrangement = Arrangement.Center) {
                LabeledTextField({ Text(code) }, code, { code = it }, placeHolder = "Code")
                HorizontalSpacer(20.dp)
                TextButton("Confirm") {
                    scope.launch {
                        codeStatus = if(controller.verifyResetCode(usernameText, code)) 1 else 2
                    }
                }
            }
            VerticalSpacer(10.dp)
            when(codeStatus) {
                1 -> {
                    var newPassword by rememberMutableStateOf("")
                    var confirmNewPassword by rememberMutableStateOf("")
                    var resetError by rememberMutableStateOf(false)
                    VerticalSpacer(30.dp)
                    LabeledTextField({ Text("New Password") }, newPassword, { newPassword = it }, placeHolder = "New password")
                    VerticalSpacer(30.dp)
                    LabeledTextField({ Text("Confirm Password") }, confirmNewPassword, { confirmNewPassword = it }, placeHolder = "Confirm password")
                    VerticalSpacer(30.dp)
                    TextButton("Reset") {
                        scope.launch {
                            resetError = newPassword != confirmNewPassword
                            resetError = controller.resetPassword(usernameText, code, newPassword)
                            if(!resetError) setNavState(Login)
                        }
                    }
                    if(resetError) {
                        VerticalSpacer(10.dp)
                        ErrorText("Something went wrong. Please try again later")
                    }
                }
                2 -> ErrorText("Invalid code. Try again")
            }
        }
    }
}