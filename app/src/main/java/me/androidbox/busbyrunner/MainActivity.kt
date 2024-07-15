package me.androidbox.busbyrunner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModel<MainViewModel>()
    private lateinit var splitInstallManager: SplitInstallManager
    private val splitInstallListener = SplitInstallStateUpdatedListener { state ->
        when(state.status()) {

            SplitInstallSessionStatus.DOWNLOADING -> {
                mainViewModel.setAnalyticsDialogVisibility(isVisible = true)
            }

            SplitInstallSessionStatus.FAILED -> {
                mainViewModel.setAnalyticsDialogVisibility(isVisible = false)
                Toast.makeText(
                    applicationContext,
                    getString(R.string.could_not_load_module),
                    Toast.LENGTH_LONG
                ).show()
            }

            SplitInstallSessionStatus.INSTALLED -> {
                mainViewModel.setAnalyticsDialogVisibility(isVisible = false)

                Toast.makeText(
                    applicationContext,
                    getString(R.string.could_not_load_module),
                    Toast.LENGTH_LONG
                ).show()
            }

            SplitInstallSessionStatus.INSTALLING -> {
                mainViewModel.setAnalyticsDialogVisibility(isVisible = true)
            }

            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                splitInstallManager.startConfirmationDialogForResult(
                    state, this, 0
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        splitInstallManager.registerListener(splitInstallListener)
    }

    override fun onPause() {
        super.onPause()
        splitInstallManager.unregisterListener(splitInstallListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BuildConfig.API_KEY
        installSplashScreen().apply {
           this.setKeepOnScreenCondition {
               mainViewModel.mainState.isAuthenticating
           }
        }

        splitInstallManager = SplitInstallManagerFactory.create(applicationContext)


        setContent {
            BusbyRunnerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {

                    if(!mainViewModel.mainState.isAuthenticating) {
                        val navHostController = rememberNavController()
                        NavigationRoot(
                            isLoggedIn = mainViewModel.mainState.isLoggedIn,
                            navHostController = navHostController,
                            onAnalyticClicked = {
                                installOrStartAnalyticFeature()
                            })

                        if(mainViewModel.mainState.showAnalyticsInstalledDialog) {
                            Dialog(onDismissRequest = {}) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(color = MaterialTheme.colorScheme.surface)
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.installing_the_analytics_module),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun installOrStartAnalyticFeature() {
        if(splitInstallManager.installedModules.contains("analytics_feature")) {
            Intent()
                .setClassName(
                    packageName,
                    "me.androidbox.analytics.analytics_feature.AnalyticsActivity"
                )
                .also(::startActivity)

            return
        }

        val request = SplitInstallRequest.newBuilder()
            .addModule("analytics_feature")
            .build()

        splitInstallManager
            .startInstall(request)
            .addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.could_not_load_module),
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}