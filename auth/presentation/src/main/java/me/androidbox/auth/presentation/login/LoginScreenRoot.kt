package me.androidbox.auth.presentation.login

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    onLoginSuccess: () -> Unit,
    onSignUpClicked: () -> Unit,
    loginViewModel: LoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ObserveAsEvents(flow = loginViewModel.loginEvent) { loginEvent ->

        when(loginEvent) {
            is LoginEvent.OnLoginFailure -> {
                keyboardController?.hide()

                Toast.makeText(context, loginEvent.error.asString(context), Toast.LENGTH_LONG).show()
            }
            LoginEvent.OnLoginSuccess -> {
                keyboardController?.hide()
                /** Navigate to home screen */
                onLoginSuccess()
            }
        }
    }

    LoginScreen(
        loginState = loginViewModel.loginState,
        onLoginAction = { loginAction ->
            when(loginAction) {
                LoginAction.OnRegisterClicked -> {
                    onSignUpClicked()
                }
                else -> {
                    loginViewModel.onLoginAction(loginAction)
                }
            }
        })
}

@Composable
@Preview
fun PreviewLoginScreenRoot() {
    BusbyRunnerTheme {
        LoginScreenRoot(
            onLoginSuccess = {},
            onSignUpClicked = {}
        )
    }
}