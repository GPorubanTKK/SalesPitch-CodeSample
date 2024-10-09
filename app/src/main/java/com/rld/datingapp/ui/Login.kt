package com.rld.datingapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.rld.datingapp.NavPosition
import com.rld.datingapp.NavPosition.ForgotPassword
import com.rld.datingapp.NavPosition.Main
import com.rld.datingapp.NavPosition.Signup
import com.rld.datingapp.data.ViewModel.Companion.controller
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.ui.util.ErrorText
import com.rld.datingapp.ui.util.HorizontalSpacer
import com.rld.datingapp.ui.util.LabeledTextField
import com.rld.datingapp.ui.util.PasswordTextField
import com.rld.datingapp.ui.util.TextButton
import com.rld.datingapp.ui.util.VerticalSpacer
import com.rld.datingapp.ui.util.maxSize
import com.rld.datingapp.ui.util.maxWidth
import com.rld.datingapp.ui.util.rememberMutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable fun Login(setNavState: (NavPosition) -> Unit, viewModel: ViewModel) {
    val scope = rememberCoroutineScope()
    var usernameText by rememberMutableStateOf("")
    var passwordText by rememberMutableStateOf("")
    var invalidLogin by rememberMutableStateOf(false)
    Column(modifier = maxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        VerticalSpacer(100.dp)
        LabeledTextField({ Text("Username:") }, usernameText, { usernameText = it }, placeHolder = "Username")
        VerticalSpacer(30.dp)
        PasswordTextField({ Text("Password:") }, passwordText, { passwordText = it }, placeHolder = "Password")
        VerticalSpacer(10.dp)
        if(invalidLogin) {
            ErrorText("Wrong username or password!")
            VerticalSpacer(20.dp)
        }
        Text(
            "Forgot password?",
            modifier = Modifier.clickable { setNavState(ForgotPassword) },
            textDecoration = TextDecoration.Underline
        )
        VerticalSpacer(20.dp)
        Row(modifier = maxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton("Sign up") { setNavState(Signup) }
            HorizontalSpacer(50.dp)
            TextButton("Login") {
                scope.launch(Dispatchers.Main) {
                    val response = withContext(Dispatchers.IO) {
                        controller.attemptLogin(usernameText, passwordText)
                    }
                    usernameText = ""
                    passwordText = ""
                    invalidLogin = false
                    if(response == null) {
                        invalidLogin = true
                    }
                    else {
                        setNavState(Main)
                        viewModel.setUser(response)
                    }
                }
            }
        }
    }
}