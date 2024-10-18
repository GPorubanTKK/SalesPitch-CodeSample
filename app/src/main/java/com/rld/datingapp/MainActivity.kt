package com.rld.datingapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.ui.ForgotPassword
import com.rld.datingapp.ui.Login
import com.rld.datingapp.ui.MainApp
import com.rld.datingapp.ui.Signup
import com.rld.datingapp.ui.theme.DatingAppTheme
import com.rld.datingapp.ui.util.rememberMutableStateOf

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    private val viewModel: ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildNotificationsChannel()
        requestCameraPermisssion { permission ->
            val preferences = getPreferences(Context.MODE_PRIVATE)
            with(preferences.edit()) {
                putBoolean("hasCameraPref", permission)
                apply()
            }
        }
        setContent {
            var navState by rememberMutableStateOf(NavPosition.Login)
            val setNavState = remember { { value: NavPosition -> navState = value } }
            DatingAppTheme {
                when(navState) {
                    NavPosition.Login -> Login(setNavState, viewModel, getPreferences(Context.MODE_PRIVATE))
                    NavPosition.Signup -> Signup(setNavState, getPreferences(Context.MODE_PRIVATE))
                    NavPosition.ForgotPassword -> ForgotPassword(setNavState)
                    NavPosition.Main -> MainApp(viewModel)
                }
            }
        }
    }

    private fun requestCameraPermisssion(callback: (Boolean) -> Unit) {
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(), callback
        ).launch("android.permission.CAMERA")
    }

    private fun buildNotificationsChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, "SalesPitch", importance)
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

const val LOGGERTAG = "SalesPitch"
const val CHANNEL_ID = "SalesPitchNotifications"

enum class NavPosition {
    Login,
    Signup,
    ForgotPassword,
    Main
}