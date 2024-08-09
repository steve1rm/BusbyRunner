package me.androidbox.wear.run.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import me.androidbox.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrackerScreenRoot() {
    val trackerViewModel = koinViewModel<TrackerViewModel>()

    val context = LocalContext.current


    ObserveAsEvents(trackerViewModel.event) { trackEvent ->
        when(trackEvent) {
            is TrackerEvent.Error -> {
                Toast.makeText(context, trackEvent.message.asString(context), Toast.LENGTH_LONG).show()
            }
            TrackerEvent.FinishTracking -> {
                Toast.makeText(context, "", Toast.LENGTH_LONG).show()
            }
            TrackerEvent.RunFinished -> {
                Toast.makeText(context, "", Toast.LENGTH_LONG).show()
            }
        }
    }

    TrackerScreen(
        trackerState = trackerViewModel.trackerState,
        trackerAction = trackerViewModel::onTrackerAction)
}