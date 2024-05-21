package me.androidbox.run.presentation.active_run

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(
    activeRunViewModel: ActiveRunViewModel = koinViewModel(),
    onServiceToggle: (isServiceRunning: Boolean) -> Unit
) {

    ActiveRunScreen(
        activeRunState = activeRunViewModel.activeRunState,
        onActiveRunAction = activeRunViewModel::onActiveRunAction,
        onServiceToggle = onServiceToggle)
}

@Composable
@Preview
fun PreviewActiveRunScreenRoot() {
    BusbyRunnerTheme {
        ActiveRunScreenRoot(
            onServiceToggle = {}
        )
    }
}