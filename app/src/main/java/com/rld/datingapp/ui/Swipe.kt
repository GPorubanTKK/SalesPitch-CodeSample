package com.rld.datingapp.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexstyl.swipeablecard.Direction
import com.alexstyl.swipeablecard.ExperimentalSwipeableCardApi
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import com.alexstyl.swipeablecard.swipableCard
import com.rld.datingapp.LOGGERTAG
import com.rld.datingapp.data.User
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.data.ViewModel.Companion.controller
import com.rld.datingapp.ui.components.ProfileCard
import com.rld.datingapp.ui.util.VerticalSpacer
import com.rld.datingapp.ui.util.maxSize
import com.rld.datingapp.ui.util.rememberMutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalSwipeableCardApi::class)
@Composable
fun Swipe(viewModel: ViewModel) = Column(
    modifier = Modifier.maxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    val usersToShow = remember { mutableStateListOf<User?>() }
    val states = usersToShow.reversed().map { it to rememberSwipeableCardState() }
    var fetching by rememberMutableStateOf(false)
    Box(modifier = Modifier.maxSize(0.8f).align(Alignment.CenterHorizontally)) {
        for((profile, state) in states) {
            if(profile != null) {
                ProfileCard(
                    viewModel.loggedInUser!!,
                    profile,
                    { profile == usersToShow[0] },
                    modifier = Modifier.maxSize().swipableCard(
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
                                        controller.matchWith(viewModel.loggedInUser!!, profile)
                                        Log.d(LOGGERTAG, "You matched with ${profile.firstName}!")
                                    } catch (ignored: NullPointerException) {}
                                }
                                else -> throw IllegalStateException("Cannot swipe any direction besides left or right.")
                            }
                            usersToShow.removeAt(0)
                            fetching = true
                            Log.d(LOGGERTAG, "Nonnull profile")
                        }
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        usersToShow.removeAt(0)
                        fetching = true
                        Log.d(LOGGERTAG, "Null profile")
                    }
                }
            }
        }
    }
    LaunchedEffect(fetching) {
        withContext(Dispatchers.IO) { while(usersToShow.size < 2 && fetching) usersToShow += controller.getNextUser(viewModel.loggedInUser!!) }
        fetching = false
    }
    LaunchedEffect(Unit) { fetching = true }
    VerticalSpacer(60.dp)
}