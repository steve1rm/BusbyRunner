package me.androidbox.auth.presentation.login

import me.androidbox.core.presentation.ui.UiText

sealed interface LoginEvent {
    data object OnLoginSuccess : LoginEvent
    data class OnLoginFailure(val error: UiText) : LoginEvent
}