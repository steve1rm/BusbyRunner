package me.androidbox.auth.presentation.auth

sealed interface IntroAction {
    data object OnSignInClicked : IntroAction
    data object OnSignUpClicked : IntroAction
}