package me.androidbox.wear.run.presentation

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrackerScreenRoot() {
    val trackerViewModel = koinViewModel<TrackerViewModel>()

    TrackerScreen(
        trackerState = trackerViewModel.trackerState,
        trackerAction = { trackerAction ->

        })
}