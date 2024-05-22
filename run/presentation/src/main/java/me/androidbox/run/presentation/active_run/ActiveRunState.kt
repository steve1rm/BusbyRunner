package me.androidbox.run.presentation.active_run

import me.androidbox.core.domain.location.Location
import me.androidbox.run.domain.RunData
import kotlin.time.Duration

/** Different states the UI can be in */
data class ActiveRunState(
    val elapsedTime: Duration = Duration.ZERO,
    val shouldTrack: Boolean = false,
    val hasStartedRunning: Boolean = false,
    val currentLocation: Location? = null,
    val isRunFinished: Boolean = false,
    val isSavingRun: Boolean = false,
    val runData: RunData = RunData(),
    val shouldShowLocationPermissionRationale: Boolean = false,
    val shouldShowNotificationPermissionRationale: Boolean = false
)
