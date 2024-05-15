@file:OptIn(ExperimentalFoundationApi::class)

package me.androidbox.auth.presentation.register

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.androidbox.auth.domain.AuthorizationRepository
import me.androidbox.auth.domain.UserDataValidator
import me.androidbox.auth.presentation.R
import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.Result
import me.androidbox.core.presentation.ui.UiText
import me.androidbox.core.presentation.ui.toUiText


class RegisterViewModel(
    private val userDataValidator: UserDataValidator,
    private val authorizationRepository: AuthorizationRepository
) : ViewModel() {

    var registerState by mutableStateOf(RegisterState())
        private set

    private val eventChannel = Channel<RegisterEvent>()
    val registrationEvent = eventChannel.receiveAsFlow()

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
                register()
            }
            RegisterAction.OnTogglePasswordVisibilityClicked -> {
                registerState = registerState.copy(
                    isPasswordVisible = !registerState.isPasswordVisible
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun register() {
        viewModelScope.launch {
            registerState = registerState.copy(isRegistering = true)

            val result = authorizationRepository.register(
                registerState.email.text.toString().trim(),
                registerState.password.text.toString()
            )

            registerState = registerState.copy(isRegistering = false)

            when(result) {
                is Result.Failure -> {
                    if(result.error == DataError.Network.CONFLICT) {
                        eventChannel.send(RegisterEvent.RegistrationFailure(
                            UiText.StringResource(resId = R.string.email_exists)
                        ))
                    }
                    else {
                        eventChannel.send(RegisterEvent.RegistrationFailure(result.error.toUiText()))
                    }
                }
                is Result.Success -> {
                    eventChannel.send(RegisterEvent.RegistrationSuccess)
                }
            }
        }
    }
}