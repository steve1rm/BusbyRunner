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
                registerState = registerState.copy(
                    isValidEmail = userDataValidator.isValidEmail(email.toString())
                )
            }
            .launchIn(viewModelScope)

        registerState.password.textAsFlow()
            .onEach { password ->
                registerState = registerState.copy(
                    passwordValidationState = userDataValidator.validatePassword(password.toString())
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