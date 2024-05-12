package me.androidbox.auth.presentation.auth

import androidx.compose.runtime.Composable

@Composable
fun IntroScreenRoot(
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    IntroScreen(
        onAction = { introAction ->
            when(introAction) {
                IntroAction.OnSignInClicked -> {
                    onSignInClicked()
                }
                IntroAction.OnSignUpClicked -> {
                    onSignUpClicked()
                }
            }
        }
    )
}