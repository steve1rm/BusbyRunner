package me.androidbox.busbyrunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
           this.setKeepOnScreenCondition {
               mainViewModel.mainState.isAuthenticating
           }
        }

        setContent {
            BusbyRunnerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {

                    if(!mainViewModel.mainState.isAuthenticating) {
                        val navHostController = rememberNavController()
                        NavigationRoot(
                            isLoggedIn = mainViewModel.mainState.isLoggedIn,
                            navHostController = navHostController)
                    }
                }
            }
        }
    }
}