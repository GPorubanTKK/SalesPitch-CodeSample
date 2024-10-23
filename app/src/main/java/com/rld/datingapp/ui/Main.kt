package com.rld.datingapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.ui.util.HorizontalSpacer
import com.rld.datingapp.ui.util.NavIcon
import com.rld.datingapp.ui.util.maxSize
import com.rld.datingapp.ui.util.maxWidth
import com.rld.datingapp.ui.util.rememberMutableStateOf

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun MainApp(viewModel: ViewModel) {
    var navState by rememberMutableStateOf(NavItem.Swipe)
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.maxWidth().padding(top = 5.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalSpacer(50.dp)
                for(item in NavItem.entries) {
                    NavIcon(
                        item.label,
                        item.icon,
                        false
                    ) { navState = item }
                    HorizontalSpacer(65.dp)
                }
            }
        }
    ) {
        Column(modifier = Modifier.maxSize().padding(horizontal = 10.dp)) {
            when(navState) {
                NavItem.Swipe -> Swipe(viewModel)
                NavItem.Messages -> Messages(viewModel)
                NavItem.Settings -> Settings(viewModel)
            }
        }
    }
}

enum class NavItem(
    val label: String,
    val icon: ImageVector
) {
    Messages(
        "Messages",
        Icons.Default.MailOutline
    ),
    Swipe(
        "Match",
        Icons.Filled.FavoriteBorder
    ),
    Settings(
        "Settings",
        Icons.Default.Settings
    ),
}

