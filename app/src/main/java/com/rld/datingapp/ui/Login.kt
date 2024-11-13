package com.rld.datingapp.ui

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.data.ViewModel.Companion.controller
import com.rld.datingapp.ui.util.ErrorText
import com.rld.datingapp.ui.util.HorizontalSpacer
import com.rld.datingapp.ui.components.LabeledTextField
import com.rld.datingapp.ui.components.PasswordTextField
import com.rld.datingapp.ui.components.TextButton
import com.rld.datingapp.ui.util.VerticalSpacer
import com.rld.datingapp.ui.util.maxSize
import com.rld.datingapp.ui.util.maxWidth
import com.rld.datingapp.ui.util.rememberMutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable fun Login(setNavState: (NavPosition) -> Unit, viewModel: ViewModel, preferences: SharedPreferences) {
    val scope = rememberCoroutineScope()
    var usernameText by rememberMutableStateOf(preferences.getString("username", "")!!)
    var passwordText by rememberMutableStateOf(preferences.getString("password", "")!!)
    var invalidLogin by rememberMutableStateOf(false)
    var isLoggingIn by rememberMutableStateOf(false)
    fun loadPrefs() = with(preferences.edit()) {
        putString("username", usernameText)
        putString("password", passwordText)
        apply()
    }
    Column(modifier = Modifier.maxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
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
        Row(modifier = Modifier.maxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton("Sign up") { setNavState(Signup) }
            HorizontalSpacer(50.dp)
            TextButton("Login", enabled = !isLoggingIn) {
                isLoggingIn = true
                scope.launch(Dispatchers.IO) {
                    val response = controller.attemptLogin(usernameText, passwordText)
                    usernameText = ""
                    passwordText = ""
                    invalidLogin = false
                    if(response == null) invalidLogin = true
                    else {
                        viewModel.loggedInUser = response
                        loadPrefs()
                        setNavState(Main)
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            if(usernameText.isNotBlank() && passwordText.isNotBlank() && !invalidLogin) {
                isLoggingIn = true
                val response = controller.attemptLogin(usernameText, passwordText)
                usernameText = ""
                passwordText = ""
                invalidLogin = false
                if(response == null) {
                    invalidLogin = true
                    isLoggingIn = false
                }
                else {
                    viewModel.loggedInUser = response
                    val matches = controller.getMatches(viewModel.loggedInUser!!)
                    viewModel.matches.addAll(matches)
                    for(match in matches) if(match.accepted) viewModel.addRecipient(match.other(viewModel.loggedInUser!!).email)
                    loadPrefs()
                    setNavState(Main)
                }
            }
        }
    }
}