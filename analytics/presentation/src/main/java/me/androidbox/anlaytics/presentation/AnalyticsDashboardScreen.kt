@file:OptIn(ExperimentalMaterial3Api::class)

package me.androidbox.anlaytics.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerScaffold
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerToolbar

@Composable
fun AnalyticsDashboardScreen(
    analyticsDashboardState: AnalyticsDashboardState?,
    onAnalyticsAction: (action: AnalyticsAction) -> Unit
) {
    BusbyRunnerScaffold(
       topAppBar = {
           BusbyRunnerToolbar(
               shouldShowBackButton = true,
               title = stringResource(R.string.analytics),
               onBackClicked = {
                   onAnalyticsAction(AnalyticsAction.OnBackClicked)
               }
           )
       },
        content = { paddingValues ->
            if(analyticsDashboardState == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        AnalyticsCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.total_distance_run),
                            value = analyticsDashboardState.totalDistanceRun
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        AnalyticsCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.total_time_run),
                            value = analyticsDashboardState.totalTimeRun
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        AnalyticsCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.fastest_ever_run),
                            value = analyticsDashboardState.fastestEverRun
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        AnalyticsCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.avg_distance_per_run),
                            value = analyticsDashboardState.avgDistance
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        AnalyticsCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.avg_pace_per_run),
                            value = analyticsDashboardState.avgPace
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )
    }
}


@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewAnalyticsCard() {
    AnalyticsCard("Avg distance per run", "00:67:46")
}

@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewAnalyticsDashboardScreen() {
    AnalyticsDashboardScreen(
        analyticsDashboardState = AnalyticsDashboardState(
            totalDistanceRun = "10.30 km",
            totalTimeRun = "16d 43h 8m",
            fastestEverRun = "124.9 km/h",
            avgPace = "3.1 km",
            avgDistance = "12:45"
        ),
        onAnalyticsAction = {}
    )
}