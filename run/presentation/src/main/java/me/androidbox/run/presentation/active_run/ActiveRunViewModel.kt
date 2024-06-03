package me.androidbox.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.androidbox.core.domain.location.Latitude
import me.androidbox.core.domain.location.Location
import me.androidbox.core.domain.location.Longitude
import me.androidbox.core.domain.run.RunModel
import me.androidbox.run.domain.LocationDataCalculator
import me.androidbox.run.domain.RunningTracker
import me.androidbox.run.presentation.active_run.service.ActiveRunService
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
) : ViewModel() {

    var activeRunState by mutableStateOf(ActiveRunState(
        shouldTrack = ActiveRunService.isServiceActive && runningTracker.isTrackingState.value,
        hasStartedRunning = ActiveRunService.isServiceActive))
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val activeRunEvent = eventChannel.receiveAsFlow()

    private val hasLocationPermission = MutableStateFlow(false)

    /** Create a flow from the compose state to track the shouldTrack state*/
    private val shouldTrack = snapshotFlow {
        activeRunState.shouldTrack
    }.stateIn(viewModelScope, SharingStarted.Lazily, activeRunState.shouldTrack)

    /** Only track if shouldTrack and the user has granted permissions */
    private val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ) { isTracking, hasLocationPermission ->
        isTracking && hasLocationPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        hasLocationPermission
            .onEach { hasPermission ->
                if(hasPermission) {
                    runningTracker.startObservingLocation()
                }
                else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)

        runningTracker
            .currentLocation
            .onEach { location ->
                activeRunState = activeRunState.copy(
                    currentLocation = location?.location
                )
            }
            .launchIn(viewModelScope)

        runningTracker
            .runDataState
            .onEach { runData ->
                activeRunState = activeRunState.copy(
                    runData = runData
                )
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTimeState
            .onEach { duration ->
                activeRunState = activeRunState
                    .copy(elapsedTime = duration)
            }
            .launchIn(viewModelScope)
       }

    fun onActiveRunAction(activeRunAction: ActiveRunAction) {
        when(activeRunAction) {
            ActiveRunAction.OnBackClicked -> {
                /** Will pause tracking if the user taps the back bottom
                 *  Can only navigate back once the user has finished the run
                 *  However, can navigate back if they haven't actually started a run */
                activeRunState = activeRunState.copy(
                    shouldTrack = false
                )
            }
            ActiveRunAction.OnFinishRunClicked -> {
                activeRunState = activeRunState.copy(
                    isRunFinished = true,
                    isSavingRun = true
                )
            }
            ActiveRunAction.OnResumeRunClicked -> {
                activeRunState = activeRunState.copy(
                    shouldTrack = true
                )
            }
            ActiveRunAction.OnToggleRunClicked -> {
                activeRunState = activeRunState.copy(
                    hasStartedRunning = true,
                    shouldTrack = !activeRunState.shouldTrack
                )
            }
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = activeRunAction.acceptedLocationPermission
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

            is ActiveRunAction.OnRunProcessed -> {
                finishedRun(activeRunAction.mapPictureBytes)
            }
        }
    }

    private fun finishedRun(mapPictureBytes: ByteArray) {
        val locations = activeRunState.runData.locations
        if(locations.isEmpty() || locations.first().size <= 1) {
            activeRunState = activeRunState.copy(
                isSavingRun = false
            )
            return
        }

        viewModelScope.launch {
            val runModel = RunModel(
                id = null,
                duration = activeRunState.elapsedTime,
                dateTimeUtc = ZonedDateTime.now()
                    .withZoneSameInstant(ZoneOffset.UTC),
                distanceMeters = activeRunState.runData.distanceMeters,
                location = activeRunState.currentLocation ?: Location(
                    Latitude(0.0), Longitude(0.0)
                ),
                maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations),
                totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                mapPictureUrl = null
            )

            // save run in repository

            runningTracker.finishedRun()
            activeRunState = activeRunState.copy(
                isSavingRun = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        if(!ActiveRunService.isServiceActive) {
            runningTracker.stopObservingLocation()
        }
    }
}