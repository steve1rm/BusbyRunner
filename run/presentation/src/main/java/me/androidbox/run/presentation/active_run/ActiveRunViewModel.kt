package me.androidbox.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.androidbox.core.connectivity.domain.messaging.MessagingAction
import me.androidbox.core.domain.location.Latitude
import me.androidbox.core.domain.location.Location
import me.androidbox.core.domain.location.Longitude
import me.androidbox.core.domain.run.RunModel
import me.androidbox.core.domain.run.RunRepository
import me.androidbox.core.domain.util.Result
import me.androidbox.core.notification.ActiveRunService
import me.androidbox.core.presentation.ui.toUiText
import me.androidbox.run.domain.LocationDataCalculator
import me.androidbox.run.domain.RunningTracker
import me.androidbox.run.domain.WatchConnector
import timber.log.Timber
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val watchConnector: WatchConnector,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var activeRunState by mutableStateOf(ActiveRunState(
        shouldTrack = ActiveRunService.isServiceActive.value && runningTracker.isTrackingState.value,
        hasStartedRunning = ActiveRunService.isServiceActive.value))
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val activeRunEvent = eventChannel.receiveAsFlow()

    private val hasLocationPermission = MutableStateFlow(false)

    /** Create a flow from the compose state to track the shouldTrack state */
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
        watchConnector.connectedDevices
            .filterNotNull()
            .onEach { connectedDevice ->
                Timber.d("new device detected: ${connectedDevice.displayName}")
                if(connectedDevice.isNearby) {

                }
            }
            .launchIn(viewModelScope)


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

        listenToWatchActions()
    }

    fun onActiveRunAction(activeRunAction: ActiveRunAction, triggeredOnWatch: Boolean = false) {
        if(!triggeredOnWatch) {
            val messagingAction = when(activeRunAction) {
                ActiveRunAction.OnFinishRunClicked -> MessagingAction.Finish
                ActiveRunAction.OnResumeRunClicked -> MessagingAction.StartOrResume
                ActiveRunAction.OnToggleRunClicked -> {
                    if(activeRunState.hasStartedRunning) {
                        MessagingAction.Pause
                    }
                    else {
                        MessagingAction.StartOrResume
                    }
                }
                else -> {
                    null
                }
            }

            messagingAction?.let { messagingAction ->
                viewModelScope.launch {
                    watchConnector.sendActionToWatch(messagingAction)
                }
            }
        }

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
                mapPictureUrl = null,
                maxHeartRate =
                if(activeRunState.runData.heartRates.isNotEmpty()) { activeRunState.runData.heartRates.max() } else { null },
                avgHeartRete =
                if(activeRunState.runData.heartRates.isNotEmpty()) {activeRunState.runData.heartRates.average().roundToInt()} else { null }
            )

            runningTracker.finishedRun()

            // save run in repository
            when(val result = runRepository.upsertRun(runModel, mapPictureBytes)) {
                is Result.Failure -> {
                    eventChannel.trySend(ActiveRunEvent.SaveRunFailure(result.error.toUiText()))
                }
                is Result.Success -> {
                    eventChannel.trySend(ActiveRunEvent.SaveRunSuccess)
                }
            }

            activeRunState = activeRunState.copy(
                isSavingRun = false
            )
        }
    }

    private fun listenToWatchActions() {
        /** Explained in video 3.9 13.22 when we only need these events
         *  */
        watchConnector.messagingActions
            .onEach { action ,->
                when(action) {
                    MessagingAction.ConnectionRequest -> {
                        /** Already started a run on the phone */
                        if(isTracking.value) {
                            watchConnector.sendActionToWatch(
                                MessagingAction.StartOrResume)
                        }
                    }
                    MessagingAction.Finish -> {
                        onActiveRunAction(
                            activeRunAction = ActiveRunAction.OnFinishRunClicked,
                            triggeredOnWatch = true)
                    }
                    MessagingAction.Pause -> {
                        if(isTracking.value) {
                            onActiveRunAction(
                                activeRunAction = ActiveRunAction.OnToggleRunClicked,
                                triggeredOnWatch = true
                            )
                        }
                    }
                    MessagingAction.StartOrResume -> {
                        if(!isTracking.value) {
                            if(activeRunState.hasStartedRunning) {
                                onActiveRunAction(
                                    activeRunAction = ActiveRunAction.OnToggleRunClicked,
                                    triggeredOnWatch = true
                                )
                            }
                            else {
                                onActiveRunAction(
                                    activeRunAction = ActiveRunAction.OnToggleRunClicked,
                                    triggeredOnWatch = true
                                )
                            }
                        }
                    }
                    else -> {
                        Unit
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        if(!ActiveRunService.isServiceActive.value) {
            /** Triggered when clicking on the back button
             * Explained 3.9 20:00 about going back active run screen */
            applicationScope.launch {
                watchConnector.sendActionToWatch(MessagingAction.UnTrackable)
            }
            runningTracker.stopObservingLocation()
        }
    }
}