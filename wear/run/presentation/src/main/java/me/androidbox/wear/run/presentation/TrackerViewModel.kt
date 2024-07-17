package me.androidbox.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.androidbox.wear.run.domain.ExerciseTracker

class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker
) : ViewModel() {

    var trackerState by mutableStateOf(TrackerState())
        private set

    private val hasBodySensorPermission = MutableStateFlow(false)

    fun onTrackerAction(trackerAction: TrackerAction) {
        when(trackerAction) {
            TrackerAction.OnFinishRunClicked -> {

            }

            TrackerAction.OnToggleRunClicked -> {

            }

            is TrackerAction.OnBodySensorPermissionResult -> {
                hasBodySensorPermission.value = trackerAction.isGranted

                if(trackerAction.isGranted) {
                    viewModelScope.launch {
                        val isHeartRateTrackingSupported = exerciseTracker.isHeartRateTrackingSupported()

                        trackerState = trackerState.copy(
                            canTrackHeartRate = isHeartRateTrackingSupported
                        )
                    }
                }
            }
        }
    }
}