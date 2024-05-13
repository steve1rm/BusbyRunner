package me.androidbox.auth.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    var registerState by mutableStateOf(RegisterState())
        private set

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