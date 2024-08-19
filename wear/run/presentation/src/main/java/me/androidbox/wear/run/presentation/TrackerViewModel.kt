package me.androidbox.wear.run.presentation

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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.androidbox.core.connectivity.domain.messaging.MessagingAction
import me.androidbox.core.domain.util.Result
import me.androidbox.core.notification.ActiveRunService
import me.androidbox.wear.run.domain.ExerciseTracker
import me.androidbox.wear.run.domain.PhoneConnector
import me.androidbox.wear.run.domain.RunningTracker
import kotlin.time.Duration

class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker,
    private val phoneConnector: PhoneConnector,
    private val runningTracker: RunningTracker
) : ViewModel() {

    /** 3.10 16:00 */
    var trackerState by mutableStateOf(TrackerState(
        hasStartedRunning = ActiveRunService.isServiceActive.value,
        isRunActive = ActiveRunService.isServiceActive.value && runningTracker.isTracking.value,
        isTrackable = ActiveRunService.isServiceActive.value
    ))
        private set

    /** Create a flow that will emit whenever either of the 3 states change */
    private val isTracking = snapshotFlow {
        trackerState.isRunActive &&
                trackerState.isTrackable &&
                trackerState.isConnectedPhoneNearBy
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val hasBodySensorPermission = MutableStateFlow(false)

    /** Sending one time events into this channel viewModel => Screen  */
    private val eventChannel = Channel<TrackerEvent>()
    val event = eventChannel.receiveAsFlow()

    init {
        phoneConnector.connectedNode
            .filterNotNull()
            .onEach { connectedNode ->
                trackerState = trackerState.copy(
                    isConnectedPhoneNearBy = connectedNode.isNearby
                )
            }
            /** combine block will emit when there is a new value for the connected deviceNode or isTracking */
            .combine(isTracking) { _, isTracking ->
                if(!isTracking) {
                    phoneConnector.sendActionToPhone(MessagingAction.ConnectionRequest)
                }
            }
            .launchIn(viewModelScope)

        runningTracker
            .isTrackable
            .onEach { isTrackable ->
                trackerState = trackerState.copy(
                    isTrackable = isTrackable
                )
            }
            .launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                val result = when {
                    isTracking && !trackerState.hasStartedRunning -> {
                        exerciseTracker.startExercise()
                    }
                    isTracking && trackerState.hasStartedRunning -> {
                        exerciseTracker.resumeExercise()
                    }
                    !isTracking && trackerState.hasStartedRunning -> {
                        exerciseTracker.pauseExercise()
                    }
                    else -> {
                        Result.Success(Unit)
                    }
                }

                when(result) {
                    is Result.Failure -> {
                        result.error.toUiText()?.let { uiText ->
                            eventChannel.send(TrackerEvent.Error(uiText))
                        }
                    }
                    is Result.Success -> { /** no-op */ }
                }

                if(isTracking) {
                    trackerState = trackerState.copy(
                        hasStartedRunning = true
                    )
                }
                runningTracker.setIsTracking(isTracking = true)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            val isHeartRateTrackingSupported = exerciseTracker.isHeartRateTrackingSupported()
            trackerState = trackerState.copy(
                canTrackHeartRate = isHeartRateTrackingSupported
            )
        }

        /** Update from watch */
        runningTracker
            .heartRate
            .onEach { heartRate ->
                trackerState = trackerState.copy(heartRate = heartRate)
            }
            .launchIn(viewModelScope)

        /** Update distance and elapsed time from phone */
        runningTracker
            .distanceMeters
            .onEach { meters ->
                trackerState = trackerState.copy(distanceMeters = meters)
            }
            .launchIn(viewModelScope)

        runningTracker
            .elalapedTime
            .onEach { duration ->
                trackerState = trackerState.copy(elapsedDuration = duration)
            }
            .launchIn(viewModelScope)

        listenToPhoneActions()
    }

    fun onTrackerAction(trackerAction: TrackerAction, isTriggeredOnPhone: Boolean = false) {
        if(isTriggeredOnPhone) {
            /** Can result in a loop 3.8 Wear 30.14 Explained in video */
            sendActionToPhone(trackerAction)
        }

        when(trackerAction) {
            TrackerAction.OnFinishRunClicked -> {
                viewModelScope.launch {
                    exerciseTracker.stopExercise()
                    eventChannel.send(TrackerEvent.RunFinished)

                    trackerState = trackerState.copy(
                        elapsedDuration = Duration.ZERO,
                        distanceMeters = 0,
                        heartRate = 0,
                        hasStartedRunning = false,
                        isRunActive = false
                    )
                }
            }

            TrackerAction.OnToggleRunClicked -> {
                if(trackerState.isTrackable) {
                    trackerState = trackerState.copy(
                        isRunActive = !trackerState.isRunActive
                    )
                }
            }

            /** This only checks the moment we grant permission */
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

    /** From tracker screen */
    private fun sendActionToPhone(trackerAction: TrackerAction) {
        viewModelScope.launch {
            val messagingAction = when(trackerAction) {
                TrackerAction.OnFinishRunClicked -> {
                    MessagingAction.Finish
                }
                TrackerAction.OnToggleRunClicked -> {
                    if(trackerState.isRunActive) {
                        MessagingAction.Pause
                    }
                    else {
                        MessagingAction.StartOrResume
                    }
                }
                is TrackerAction.OnBodySensorPermissionResult -> null /** why this can't be null, */
            }

            messagingAction?.let { messagingAction ->
                val result = phoneConnector.sendActionToPhone(messagingAction)

                if(result is Result.Failure) {
                    println(result.error)
                }
            }
        }
    }

    private fun listenToPhoneActions() {
        phoneConnector
            .messagingActions
            .onEach { messagingAction ->
                when(messagingAction) {
                    MessagingAction.Finish -> {
                        onTrackerAction(TrackerAction.OnFinishRunClicked, isTriggeredOnPhone = true)
                    }
                    MessagingAction.Pause -> {
                        /** If we pause on the phone we need to pause it on the watch */
                        if(trackerState.isTrackable) {
                            trackerState = trackerState.copy(
                                isRunActive = false)
                        }
                    }
                    MessagingAction.StartOrResume -> {
                        if(trackerState.isTrackable) {
                            trackerState = trackerState.copy(
                                isRunActive = true)
                        }
                    }
                    MessagingAction.Trackable -> {
                        trackerState = trackerState.copy(
                            isTrackable = true
                        )
                    }
                    MessagingAction.UnTrackable -> {
                        /** If we don't grant permission on the phone */
                        trackerState = trackerState.copy(
                            isTrackable = false
                        )
                    }
                    else -> {
                        Unit
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}