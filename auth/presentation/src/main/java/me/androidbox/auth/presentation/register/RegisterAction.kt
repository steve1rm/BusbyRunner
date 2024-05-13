package me.androidbox.auth.presentation.register

sealed interface RegisterAction {
    data object OnTogglePasswordVisibilityClicked : RegisterAction
    data object OnRegisterClicked : RegisterAction
    data object OnLoginClicked : RegisterAction

}