package me.androidbox.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TrackerViewModel : ViewModel() {

    var trackerState by mutableStateOf(TrackerState())
        private set

    fun onTrackerAction(trackerAction: TrackerAction) {
        when(trackerAction) {
            TrackerAction.OnFinishRunClicked -> {

            }

            TrackerAction.OnToggleRunClicked -> {

            }
        }
    }
}