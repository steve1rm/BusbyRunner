package me.androidbox.run.presentation.active_run

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.RunIcon
import me.androidbox.core.presentation.designsystem.StartIcon
import me.androidbox.core.presentation.designsystem.StopIcon
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerFloatingActionButton
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerScaffold
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerToolbar
import me.androidbox.run.domain.RunData
import me.androidbox.run.presentation.R
import me.androidbox.run.presentation.active_run.components.RunDataCard
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRunScreen(
    modifier: Modifier = Modifier,
    activeRunState: ActiveRunState,
    activeRunAction: (activeRunAction: ActiveRunAction) -> Unit
) {
    BusbyRunnerScaffold(
        modifier = modifier,
        withGradient = false,
        topAppBar = {
            BusbyRunnerToolbar(
                shouldShowBackButton = true,
                title = stringResource(R.string.active_run),
                onBackClicked = {
                    activeRunAction(ActiveRunAction.OnBackClicked)
                }
            )
        },
        floatActionButton = {
            BusbyRunnerFloatingActionButton(
                icon = if(activeRunState.shouldTrack) StopIcon else StartIcon,
                iconSize = 20.dp,
                onButtonClicked = {})
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                RunDataCard(
                    elapsedTime = activeRunState.elapsedTime,
                    runData = activeRunState.runData,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(paddingValues)
                        .fillMaxWidth()
                )
            }
        }
    )
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
fun PreviewActiveRunScreen() {
    BusbyRunnerTheme {
        ActiveRunScreen(
            activeRunState = ActiveRunState(
               runData = RunData(
                    distanceMeters = 6783,
                    pace = 3.minutes
                )
            ),
            activeRunAction = {}
        )
    }
}