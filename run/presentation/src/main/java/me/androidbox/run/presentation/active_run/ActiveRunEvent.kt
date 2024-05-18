package me.androidbox.run.presentation.active_run

import me.androidbox.core.presentation.ui.UiText

/** Events from ViewModel => Screen */
sealed interface ActiveRunEvent {
    data class SaveRunFailure(val error: UiText) : ActiveRunEvent
    data object SaveRunSuccess : ActiveRunEvent
}
