package me.androidbox.auth.presentation.register

import me.androidbox.core.presentation.ui.UiText

sealed interface RegisterEvent {
    data object RegistrationSuccess : RegisterEvent
    data class RegistrationFailure(val error: UiText) : RegisterEvent
}
