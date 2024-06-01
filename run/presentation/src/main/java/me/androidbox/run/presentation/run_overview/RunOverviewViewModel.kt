package me.androidbox.run.presentation.run_overview

import androidx.lifecycle.ViewModel

class RunOverviewViewModel : ViewModel() {

    fun runOverviewAction(runOverviewAction: RunOverviewAction) {
        when(runOverviewAction) {
            RunOverviewAction.OnAnalyticsClicked -> {

            }
            RunOverviewAction.OnLogoutClicked -> {

            }
            RunOverviewAction.OnStartClicked -> {

            }
        }
    }
}