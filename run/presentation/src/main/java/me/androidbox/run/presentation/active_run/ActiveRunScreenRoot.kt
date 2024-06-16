package me.androidbox.run.presentation.active_run

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(
    activeRunViewModel: ActiveRunViewModel = koinViewModel(),
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onBackClicked: () -> Unit,
    onRunFinished: () -> Unit
) {
    val localContext = LocalContext.current

    ObserveAsEvents(flow = activeRunViewModel.activeRunEvent) { event ->
        when(event) {
            is ActiveRunEvent.SaveRunFailure -> {
                Toast.makeText(localContext, event.error.asString(localContext), Toast.LENGTH_LONG).show()
            }
            ActiveRunEvent.SaveRunSuccess -> {
                onRunFinished()
            }
        }
    }

    ActiveRunScreen(
        activeRunState = activeRunViewModel.activeRunState,
        onActiveRunAction = { action ->
            when(action) {
                /** Handle only the action we need mostly for navigation */
                is ActiveRunAction.OnBackClicked -> {
                    if(!activeRunViewModel.activeRunState.hasStartedRunning) {
                        onBackClicked()
                    }
                }
                else -> {
                    Unit
                }
            }
            /** Forward all other to the view model */
            activeRunViewModel.onActiveRunAction(action)
        },
        onServiceToggle = onServiceToggle)
}

@Composable
@Preview
fun PreviewActiveRunScreenRoot() {
    BusbyRunnerTheme {
        ActiveRunScreenRoot(
            onServiceToggle = {},
            onRunFinished = {},
            onBackClicked = {}
        )
    }
}