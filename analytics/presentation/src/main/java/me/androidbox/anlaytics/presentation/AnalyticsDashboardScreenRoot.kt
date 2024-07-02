package me.androidbox.anlaytics.presentation

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsDashboardScreenRoot(
    analyticsDashboardViewModel: AnalyticsDashboardViewModel = koinViewModel(),
    onBackClicked: () -> Unit
) {

    AnalyticsDashboardScreen(
        analyticsDashboardViewModel.analyticsDashboardState,
        onAnalyticsAction = { action ->
            when(action) {
                is AnalyticsAction.OnBackClicked -> {
                    onBackClicked()
                }
            }
        }
    )
}