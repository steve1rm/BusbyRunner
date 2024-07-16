package me.androidbox.wear.run.presentation

/** Event from ViewModel ==> UI Screen */
sealed interface TrackerEvent {
    data object FinishTracking : TrackerEvent
}