/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package me.androidbox.wear.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import me.androidbox.core.notification.ActiveRunService
import me.androidbox.core.presentation.designsystem_wear.BusbyRunnerTheme
import me.androidbox.wear.run.presentation.TrackerScreenRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            BusbyRunnerTheme {
                TrackerScreenRoot(
                    onServiceToggle = { shouldStartRunning ->
                        if(shouldStartRunning) {
                            startService(
                                ActiveRunService.createStartIntent(
                                    applicationContext, MainActivity::class.java
                                )
                            )
                        }
                        else {
                            startService(
                                ActiveRunService.createStopIntent(
                                    applicationContext
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}
