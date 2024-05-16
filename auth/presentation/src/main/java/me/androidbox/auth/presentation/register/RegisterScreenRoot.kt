package me.androidbox.auth.presentation.register

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import me.androidbox.auth.presentation.R
import me.androidbox.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreenRoot(
    onSignUpClicked: () -> Unit,
    onSuccessfulSignUp: () -> Unit,
    registerViewModel: RegisterViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

   ObserveAsEvents(flow = registerViewModel.registrationEvent) { registerEvent ->
       when(registerEvent) {
           is RegisterEvent.RegistrationFailure -> {
               keyboardController?.hide()
               Toast.makeText(context, registerEvent.error.asString(context), Toast.LENGTH_LONG).show()
           }
           RegisterEvent.RegistrationSuccess -> {
               keyboardController?.hide()
               Toast.makeText(context, R.string.registration_success, Toast.LENGTH_LONG).show()
               onSuccessfulSignUp()
           }
       }
   }

    RegisterScreen(
        registerState = registerViewModel.registerState,
        onAction = { registerAction ->
            when(registerAction) {
                RegisterAction.OnLoginClicked -> {
                    onSignUpClicked()
                }
                else -> {
                    registerViewModel.onAction(registerAction)
                }
            }
        })
}