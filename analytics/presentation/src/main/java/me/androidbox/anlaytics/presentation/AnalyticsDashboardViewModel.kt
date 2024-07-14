package me.androidbox.anlaytics.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.androidbox.analytics.domain.AnalyticsRepository

class AnalyticsDashboardViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    var analyticsDashboardState by mutableStateOf<AnalyticsDashboardState?>(null)
        private set

    init {
        viewModelScope.launch {
            val analyticsValues = analyticsRepository.getAnalyticsValues()
            val analyticsState = analyticsValues.toAnalyticsDashboardState()

            analyticsDashboardState = analyticsState
        }
    }
}