package me.androidbox.run.presentation.run_overview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    runOverviewViewModel: RunOverviewViewModel = koinViewModel(),
    onStartRunClicked: () -> Unit,
) {

    RunOverviewScreen(
        runOverviewAction = { runOverviewAction ->
            when(runOverviewAction) {
                RunOverviewAction.OnStartClicked -> {
                    onStartRunClicked()
                }
                else -> {
                    runOverviewViewModel.runOverviewAction(runOverviewAction)
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun PreviewRunOverviewScreenRoot() {
    BusbyRunnerTheme {
        RunOverviewScreenRoot(
            onStartRunClicked = {}
        )
    }
}

