package me.androidbox.run.presentation.active_run

/** Actions the user can perform on the screen */
sealed interface ActiveRunAction {
    data object OnToggleRunClicked : ActiveRunAction
    data object OnFinishRunClicked : ActiveRunAction
    data object OnResumeRunClicked : ActiveRunAction
    data object OnBackClicked : ActiveRunAction
    data class SubmitLocationPermissionInfo(
        val acceptedLocationPermission: Boolean,
        val showLocationRationale: Boolean
    ) : ActiveRunAction
    data class SubmitNotificationPermissionInfo(
        val acceptedNotificationPermission: Boolean,
        val showNotificationRationale: Boolean
    ) : ActiveRunAction
    data object DismissRationalDialog: ActiveRunAction
}
