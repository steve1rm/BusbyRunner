package me.androidbox.anlaytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClicked : AnalyticsAction
}
