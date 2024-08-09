package me.androidbox.wear.run.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import me.androidbox.core.connectivity.domain.messaging.MessagingAction
import kotlin.time.Duration

/** Singleton class to track runs that outlive the active run viewmodel and also when using the foreground service
 * that keeps the app alive in the background. If the app is closed it will continue to track */
class RunningTracker(
    private val watchToPhoneConnector: PhoneConnector,
    private val exerciseTracker: ExerciseTracker,
    applicationScope: CoroutineScope
) {

    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _isTrackable = MutableStateFlow(false)
    val isTrackable = _isTrackable.asStateFlow()

    val distanceMeters = watchToPhoneConnector
        .messagingActions
        .filterIsInstance<MessagingAction.DistanceUpdate>()
        .map { distanceUpdate -> distanceUpdate.distanceMeters }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            0)

    val elalapedTime = watchToPhoneConnector
        .messagingActions
        .filterIsInstance<MessagingAction.TimeUpdate>()
        .map { timeUpdate -> timeUpdate.elapsedDuration }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            Duration.ZERO)

    init {
        watchToPhoneConnector
            .messagingActions
            .onEach { action ->
                when(action) {
                    MessagingAction.Trackable -> {
                        _isTrackable.value = true
                    }
                    MessagingAction.UnTrackable -> {
                        _isTrackable.value = false
                    }
                    else -> {
                        /** no-op */
                    }
                }
            }
            .launchIn(applicationScope)

        watchToPhoneConnector
            .connectedNode
            .filterNotNull()
            .onEach {
                exerciseTracker.prepareExercise()
            }
            .launchIn(applicationScope)

        isTracking
            .flatMapLatest { isTracking ->
                if(isTracking) {
                    exerciseTracker.heartRate
                }
                else {
                    emptyFlow()
                }
            }
            .onEach { currentHeartRate ->
                watchToPhoneConnector.sendActionToPhone(MessagingAction.HeartRateUpdate(currentHeartRate))
                _heartRate.value = currentHeartRate
            }
            .launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        _isTracking.value = isTracking
    }
}