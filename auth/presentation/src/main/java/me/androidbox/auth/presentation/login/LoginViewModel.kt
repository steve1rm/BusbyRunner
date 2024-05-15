@file:OptIn(ExperimentalFoundationApi::class)

package me.androidbox.auth.presentation.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.androidbox.auth.domain.AuthorizationRepository
import me.androidbox.auth.domain.UserDataValidator
import me.androidbox.core.domain.util.Result
import me.androidbox.core.presentation.ui.toUiText

class LoginViewModel(
    private val authorizationRepository: AuthorizationRepository
) : ViewModel() {

    var loginState by mutableStateOf(LoginState())
        private set

    private val eventLoginChannel = Channel<LoginEvent>()
    val loginEvent = eventLoginChannel.receiveAsFlow()

    fun onLoginAction(action: LoginAction) {
        when(action) {
            LoginAction.OnLoginClicked -> {
                login()
            }
            LoginAction.OnRegisterClicked -> {

            }
            LoginAction.OnTogglePasswordVisibility -> {
                loginState = loginState.copy(
                    isPasswordVisible = !loginState.isPasswordVisible
                )
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            loginState = loginState.copy(isLoggingIn = true)

            val result = authorizationRepository.login(
                email = loginState.email.text.toString().trim(),
                password = loginState.password.text.toString()
            )

            when(result) {
                is Result.Failure -> {
                    /** Display toast message */
                    eventLoginChannel.send(LoginEvent.OnLoginFailure(result.error.toUiText()))

                }
                is Result.Success -> {
                    /** Go to home page */
                    eventLoginChannel.send(LoginEvent.OnLoginSuccess)
                }
            }

            loginState = loginState.copy(isLoggingIn = false)
        }
    }

}