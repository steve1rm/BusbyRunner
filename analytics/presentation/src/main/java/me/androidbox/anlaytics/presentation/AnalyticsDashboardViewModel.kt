package me.androidbox.anlaytics.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AnalyticsDashboardViewModel : ViewModel() {

    var analyticsDashboardState by mutableStateOf<AnalyticsDashboardState?>(null)
        private set

}