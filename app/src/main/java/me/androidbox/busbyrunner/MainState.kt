package me.androidbox.busbyrunner

data class MainState(
    val isLoggedIn: Boolean = false,
    val isAuthenticating: Boolean = false,
    val showAnalyticsInstalledDialog: Boolean = false
)
