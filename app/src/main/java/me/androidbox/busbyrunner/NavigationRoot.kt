package me.androidbox.busbyrunner

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navigation
import me.androidbox.auth.presentation.auth.IntroScreenRoot
import me.androidbox.auth.presentation.login.LoginScreenRoot
import me.androidbox.auth.presentation.register.RegisterScreenRoot

@Composable
fun NavigationRoot(
    isLoggedIn: Boolean,
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = if(isLoggedIn) "run" else "auth"
    ) {
        runGraph(navHostController)
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
            LoginScreenRoot(
                onLoginSuccess = {
                    navHostController.navigate("run") {
                        this.popUpTo("auth") {
                            this.inclusive = true
                        }

                    }
                },
                onSignUpClicked = {
                    navHostController.navigate("register") {
                        this.popUpTo("login") {
                            this.inclusive = true
                            this.saveState = true
                        }
                        this.restoreState = true
                    }
                }
            )
        }
    }
}


private fun NavGraphBuilder.runGraph(navHostController: NavHostController) {
    this.navigation(
        startDestination = "run_overview",
        route = "run"
    ) {
        composable(route = "run_overview") {
            Text(text = "This is the run screen", fontSize = 24.sp)
        }
    }
}