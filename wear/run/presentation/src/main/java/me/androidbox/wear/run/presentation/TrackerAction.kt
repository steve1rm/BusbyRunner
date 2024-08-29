package me.androidbox.wear.run.presentation

sealed interface TrackerAction {
    data object OnToggleRunClicked : TrackerAction
    data object OnFinishRunClicked : TrackerAction
    data class OnBodySensorPermissionResult(val isGranted: Boolean) : TrackerAction
    data class OnEnterAmbientMode(val burnInProtectionRequired: Boolean) : TrackerAction
    data object OnExitAmbientMode : TrackerAction
}
