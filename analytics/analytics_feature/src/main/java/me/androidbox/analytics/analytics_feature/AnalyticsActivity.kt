package me.androidbox.analytics.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitcompat.SplitCompat
import me.androidbox.analytics.data.di.analyticsDataModule
import me.androidbox.anlaytics.presentation.AnalyticsDashboardScreenRoot
import me.androidbox.anlaytics.presentation.di.analyticPresentationModule
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import org.koin.core.context.loadKoinModules

class AnalyticsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(
            listOf(analyticsDataModule, analyticPresentationModule))

        SplitCompat.installActivity(this)

        setContent {
            BusbyRunnerTheme {
                NavHost(
                    navController = rememberNavController(),
                    startDestination = "analytics_dashboard"
                ) {
                    composable("analytics_dashboard") {
                        AnalyticsDashboardScreenRoot(
                            onBackClicked = {
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}