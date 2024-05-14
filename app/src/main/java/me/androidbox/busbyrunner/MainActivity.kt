package me.androidbox.busbyrunner

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import me.androidbox.auth.domain.PatternValidator
import me.androidbox.auth.presentation.auth.IntroScreen
import me.androidbox.auth.presentation.register.RegisterScreen
import me.androidbox.auth.presentation.register.RegisterScreenRoot
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.analyticIcon

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BusbyRunnerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {

                 //   RegisterScreenRoot()
                }
            }
        }
    }
}