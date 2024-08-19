package me.androidbox.wear.run.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.androidbox.core.notification.ActiveRunService
import me.androidbox.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrackerScreenRoot(
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
) {
    val trackerViewModel = koinViewModel<TrackerViewModel>()
    val context = LocalContext.current

    val isServiceActive = ActiveRunService.isServiceActive.collectAsStateWithLifecycle()

    LaunchedEffect(
        key1 = trackerViewModel.trackerState.isRunActive && trackerViewModel.trackerState.hasStartedRunning,
        isServiceActive
    ) {
        if(trackerViewModel.trackerState.isRunActive && !isServiceActive.value) {
            onServiceToggle(true)
        }
    }

    ObserveAsEvents(trackerViewModel.event) { trackEvent ->
        when(trackEvent) {
            is TrackerEvent.Error -> {
                Toast.makeText(context, trackEvent.message.asString(context), Toast.LENGTH_LONG).show()
            }
            TrackerEvent.FinishTracking -> {
                Toast.makeText(context, "", Toast.LENGTH_LONG).show()
            }
            TrackerEvent.RunFinished -> {
                onServiceToggle(false)
                Toast.makeText(context, "", Toast.LENGTH_LONG).show()
            }
        }
    }

    TrackerScreen(
        trackerState = trackerViewModel.trackerState,
        trackerAction = trackerViewModel::onTrackerAction)
}