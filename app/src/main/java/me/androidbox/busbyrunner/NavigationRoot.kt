package me.androidbox.busbyrunner

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import me.androidbox.auth.presentation.auth.IntroScreenRoot
import me.androidbox.auth.presentation.register.RegisterScreenRoot

@Composable
fun NavigationRoot(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = "auth"
    ) {
        authGraph(navHostController)
    }
}

private fun NavGraphBuilder.authGraph(navHostController: NavHostController) {
    this.navigation(
        startDestination = "intro",
        route = "auth"
    ) {
        composable(route = "intro") {
            IntroScreenRoot(
                onSignInClicked = {
                    navHostController.navigate("login")
                },
                onSignUpClicked = {
                    navHostController.navigate("register")
                }
            )
        }

        composable(route = "register") {
            RegisterScreenRoot(
                onSignUpClicked = {
                    navHostController.navigate("login") {
                        this.popUpTo("register") {
                            this.inclusive = true
                            this.saveState = true
                        }
                        this.restoreState = true
                    }

                },
                onSuccessfulSignUp = {
                    navHostController.navigate("login")
                },
            )
        }

        composable(route = "login") {
            Text(text = "Login success")
        }
    }
}