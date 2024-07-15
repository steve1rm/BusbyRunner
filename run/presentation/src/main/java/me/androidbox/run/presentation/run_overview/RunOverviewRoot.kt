package me.androidbox.run.presentation.run_overview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    runOverviewViewModel: RunOverviewViewModel = koinViewModel(),
    onLogoutClicked: () -> Unit,
    onStartRunClicked: () -> Unit,
    onAnalyticClicked: () -> Unit
) {

    RunOverviewScreen(
        runOverviewAction = { runOverviewAction ->
            when(runOverviewAction) {
                RunOverviewAction.OnStartClicked -> {
                    onStartRunClicked()
                }
                RunOverviewAction.OnLogoutClicked -> {
                    onLogoutClicked()
                }
                RunOverviewAction.OnAnalyticsClicked -> {
                    onAnalyticClicked()
                }
                else -> {
                    runOverviewViewModel.runOverviewAction(runOverviewAction)
                }
            }
        },
        runOverviewState = runOverviewViewModel.runOverviewState
    )
}

@Composable
@Preview(showBackground = true)
fun PreviewRunOverviewScreenRoot() {
    BusbyRunnerTheme {
        RunOverviewScreenRoot(
            onStartRunClicked = {},
            onLogoutClicked = {},
            onAnalyticClicked = {}
        )
    }
}

