package me.androidbox.wear.run.presentation

import me.androidbox.core.presentation.ui.UiText

/** Event from ViewModel ==> UI Screen */
sealed interface TrackerEvent {
    data object FinishTracking : TrackerEvent
    data object RunFinished : TrackerEvent
    data class Error(val message: UiText) : TrackerEvent
}