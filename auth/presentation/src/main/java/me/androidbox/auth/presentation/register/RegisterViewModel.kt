@file:OptIn(ExperimentalFoundationApi::class)

package me.androidbox.auth.presentation.register

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.androidbox.auth.domain.UserDataValidator


class RegisterViewModel(
    private val userDataValidator: UserDataValidator
) : ViewModel() {

    var registerState by mutableStateOf(RegisterState())
        private set

    init {
        registerState.email.textAsFlow()
            .onEach { email ->
                val isValidEmail = userDataValidator.isValidEmail(email.toString())
                val canRegister = isValidEmail && registerState.passwordValidationState.isValidPassword && !registerState.isRegistering

                registerState = registerState.copy(
                    isValidEmail = isValidEmail,
                    canRegister = canRegister
                )
            }
            .launchIn(viewModelScope)

        registerState.password.textAsFlow()
            .onEach { password ->
                val passwordValidationState = userDataValidator.validatePassword(password.toString())
                val canRegister = registerState.isValidEmail && passwordValidationState.isValidPassword && !registerState.isRegistering

                registerState = registerState.copy(
                    passwordValidationState = passwordValidationState,
                    canRegister = canRegister
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(registerAction: RegisterAction) {
        when(registerAction) {
            RegisterAction.OnLoginClicked -> {

            }
            RegisterAction.OnRegisterClicked -> {

            }
            RegisterAction.OnTogglePasswordVisibilityClicked -> {

            }
        }
    }
}