package me.androidbox.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ActiveRunViewModel : ViewModel() {

    var activeRunState by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val activeRunEvent = eventChannel.receiveAsFlow()

    fun activeRunAction(activeRunAction: ActiveRunAction) {
        when(activeRunAction) {
            ActiveRunAction.OnBackClicked -> {

            }
            ActiveRunAction.OnFinishRunClicked -> {

            }
            ActiveRunAction.OnResumeRunClicked -> {

            }
            ActiveRunAction.OnToggleRunClicked -> {

            }
        }
    }
}