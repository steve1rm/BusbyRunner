package me.androidbox.auth.presentation.register

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreenRoot(
    onSignUpClicked: () -> Unit,
    onSuccessfullSign: () -> Unit,
    registerViewModel: RegisterViewModel = koinViewModel(),
) {

    RegisterScreen(
        registerState = registerViewModel.registerState,
        onAction = { registerAction ->
            registerViewModel.onAction(registerAction)
        })
}