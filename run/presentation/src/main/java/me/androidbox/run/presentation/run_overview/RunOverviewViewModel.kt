package me.androidbox.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.androidbox.core.domain.run.RunRepository
import me.androidbox.run.presentation.run_overview.mappers.toRunUi

class RunOverviewViewModel(
    private val runRepository: RunRepository
) : ViewModel() {

    var runOverviewState by mutableStateOf(RunOverviewState())
        private set

    init {
        runRepository.getRuns()
            .onEach { listOfRuns ->
                val runUi = listOfRuns.map {
                    runModel -> runModel.toRunUi()
                }

                runOverviewState = runOverviewState.copy(
                    listOfRuns = runUi
                )
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            /** If new runs are added remotely, this will fetch and insert them in the room
             *  which will trigger `getRuns()` as we are observing changes from room db.
             *  So the above getRuns() will update the state with new runs */
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }

    fun runOverviewAction(runOverviewAction: RunOverviewAction) {
        when(runOverviewAction) {
            RunOverviewAction.OnAnalyticsClicked -> {

            }
            RunOverviewAction.OnLogoutClicked -> {

            }
            RunOverviewAction.OnStartClicked -> {

            }

            is RunOverviewAction.OnDeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(runOverviewAction.runID)
                }
            }
        }
    }
}