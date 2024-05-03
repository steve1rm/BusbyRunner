package me.androidbox.busbyrunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import me.androidbox.busbyrunner.ui.theme.BusbyRunnerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BusbyRunnerTheme {
                Text(text = "Hello, World!")

                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }*/
            }
        }
    }
}
