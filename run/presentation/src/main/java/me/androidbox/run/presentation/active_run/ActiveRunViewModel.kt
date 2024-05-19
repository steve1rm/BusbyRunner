package me.androidbox.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import me.androidbox.run.domain.RunningTracker
import timber.log.Timber

class ActiveRunViewModel(
    private val runningTracker: RunningTracker
) : ViewModel() {

    var activeRunState by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val activeRunEvent = eventChannel.receiveAsFlow()

    private val _hasLocationPermission = MutableStateFlow(false)

    init {
        _hasLocationPermission
            .onEach { hasPermission ->
                if(hasPermission) {
                    runningTracker.startObservingLocation()
                }
                else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        runningTracker.currentLocation
            .onEach { location ->
                Timber.d("New Location: $location")
            }
            .launchIn(viewModelScope)
    }

    fun onActiveRunAction(activeRunAction: ActiveRunAction) {
        when(activeRunAction) {
            ActiveRunAction.OnBackClicked -> {

            }
            ActiveRunAction.OnFinishRunClicked -> {

            }
            ActiveRunAction.OnResumeRunClicked -> {

            }
            ActiveRunAction.OnToggleRunClicked -> {

            }
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                _hasLocationPermission.value = activeRunAction.acceptedLocationPermission
                activeRunState = activeRunState.copy(
                    shouldShowLocationPermissionRationale = activeRunAction.showLocationRationale
                )
            }
            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                activeRunState = activeRunState.copy(
                    shouldShowNotificationPermissionRationale = activeRunState.shouldShowNotificationPermissionRationale
                )
            }
            ActiveRunAction.DismissRationalDialog -> {
                activeRunState = activeRunState.copy(
                    shouldShowLocationPermissionRationale = false,
                    shouldShowNotificationPermissionRationale = false
                )
            }
        }
    }
}