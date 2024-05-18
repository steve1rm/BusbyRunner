package me.androidbox.run.presentation.active_run

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot() {
    val activeRunViewModel: ActiveRunViewModel = koinViewModel()

    ActiveRunScreen(
        activeRunState = activeRunViewModel.activeRunState,
        activeRunAction = activeRunViewModel::activeRunAction)
}

@Composable
@Preview
fun PreviewActiveRunScreenRoot() {
    BusbyRunnerTheme {
        ActiveRunScreenRoot()
    }
}