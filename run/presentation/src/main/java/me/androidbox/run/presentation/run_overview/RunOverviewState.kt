package me.androidbox.run.presentation.run_overview

import me.androidbox.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val listOfRuns: List<RunUi> = emptyList()
)
