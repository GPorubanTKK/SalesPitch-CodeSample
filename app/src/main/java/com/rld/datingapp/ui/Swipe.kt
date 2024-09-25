package com.rld.datingapp.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.alexstyl.swipeablecard.Direction
import com.alexstyl.swipeablecard.ExperimentalSwipeableCardApi
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import com.alexstyl.swipeablecard.swipableCard
import com.rld.datingapp.LOGGERTAG
import com.rld.datingapp.data.ViewModel.Companion.controller
import com.rld.datingapp.data.User
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.ui.util.ProfileCard
import com.rld.datingapp.ui.util.maxSize
import com.rld.datingapp.ui.util.maxWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalSwipeableCardApi::class)
@Composable
fun Swipe(viewModel: ViewModel) = Column(
    modifier = maxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    val usersToShow = remember { mutableStateListOf<User?>() }
    val states = usersToShow.reversed().map { it to rememberSwipeableCardState() }
    val user = viewModel.user.observeAsState()
    Box(modifier = maxWidth(0.8).fillMaxHeight(0.8f).align(Alignment.CenterHorizontally)) {
        for((profile, state) in states) {
            if(profile != null) {
                ProfileCard(
                    profile,
                    /*
                    Code taken from code sample by alyxstyl on github https://github.com/alexstyl/compose-tinder-card
                     */
                    modifier = maxSize().swipableCard(
                        state,
                        onSwiped = {},
                        onSwipeCancel = {},
                        blockedDirections = listOf(Direction.Up, Direction.Down)
                    )
                )
                LaunchedEffect(profile, state.swipedDirection) {
                    withContext(Dispatchers.IO) {
                        val direction = state.swipedDirection
                        if (direction != null) {
                            when (direction) {
                                Direction.Left -> {}
                                Direction.Right -> {
                                    try {
                                        controller.matchWith(user.value!!, profile)
                                        Log.d(LOGGERTAG, "You matched with ${profile.firstname}!")
                                    } catch (ignored: NullPointerException) {}
                                }
                                else -> throw IllegalStateException("Cannot swipe any direction besides left or right.")
                            }
                            usersToShow.removeAt(0)
                            usersToShow += controller.getNextUser()
                            Log.d(LOGGERTAG, "Nonnull profile")
                        }
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    usersToShow.removeAt(0)
                    usersToShow += controller.getNextUser()
                    Log.d(LOGGERTAG, "Null profile")
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            if (usersToShow.isEmpty()) usersToShow += listOf(
                controller.getNextUser(), controller.getNextUser()
            )
        }
    }
}