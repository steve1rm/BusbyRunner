package me.androidbox.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.StartIcon
import me.androidbox.core.presentation.designsystem.StopIcon
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerDialog
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerFloatingActionButton
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerOutlineActionButton
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerScaffold
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerToolbar
import me.androidbox.run.domain.RunData
import me.androidbox.run.presentation.R
import me.androidbox.run.presentation.active_run.components.RunDataCard
import me.androidbox.run.presentation.active_run.service.ActiveRunService
import me.androidbox.run.presentation.map.TrackerMap
import me.androidbox.run.presentation.util.hasLocationPermission
import me.androidbox.run.presentation.util.hasNoticationPermission
import me.androidbox.run.presentation.util.shouldShowLocationPermissionRationale
import me.androidbox.run.presentation.util.shouldShowNotificationPermissionRationale
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRunScreen(
    modifier: Modifier = Modifier,
    activeRunState: ActiveRunState,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onActiveRunAction: (activeRunAction: ActiveRunAction) -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        val hasCourseLocationPermission = permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val hasFineLocationPermission = permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val hasNotificationPermission = if(Build.VERSION.SDK_INT >= 33) {
            permission[Manifest.permission.POST_NOTIFICATIONS] == true
        }
        else {
            true
        }

        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onActiveRunAction(ActiveRunAction.SubmitLocationPermissionInfo(
            acceptedLocationPermission = hasCourseLocationPermission && hasFineLocationPermission,
            showLocationRationale = showLocationRationale
        ))

        onActiveRunAction(ActiveRunAction.SubmitNotificationPermissionInfo(
            acceptedNotificationPermission = hasNotificationPermission,
            showNotificationRationale = showNotificationRationale
        ))
    }

    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onActiveRunAction(ActiveRunAction.SubmitLocationPermissionInfo(
            acceptedLocationPermission = context.hasLocationPermission(),
            showLocationRationale = showLocationRationale
        ))

        onActiveRunAction(ActiveRunAction.SubmitNotificationPermissionInfo(
            acceptedNotificationPermission = context.hasNoticationPermission(),
            showNotificationRationale = showNotificationRationale
        ))

        if(!showLocationRationale && !showNotificationRationale) {
            permissionLauncher.requestBusbyRunnerPermissions(context)
        }
    }

    LaunchedEffect(key1 = activeRunState.shouldTrack) {
        if(context.hasLocationPermission()
            && activeRunState.shouldTrack
            && !ActiveRunService.isServiceActive) {

            onServiceToggle(true)
        }
    }

    LaunchedEffect(key1 = activeRunState.isRunFinished) {
        if(activeRunState.isRunFinished) {
            onServiceToggle(false)
        }
    }

    BusbyRunnerScaffold(
        modifier = modifier,
        withGradient = false,
        topAppBar = {
            BusbyRunnerToolbar(
                shouldShowBackButton = true,
                title = stringResource(R.string.active_run),
                onBackClicked = {
                    onActiveRunAction(ActiveRunAction.OnBackClicked)
                }
            )
        },
        floatActionButton = {
            BusbyRunnerFloatingActionButton(
                icon = if(activeRunState.shouldTrack) StopIcon else StartIcon,
                iconSize = 20.dp,
                onButtonClicked = {})
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {

                TrackerMap(
                    isRunFinished = activeRunState.isRunFinished,
                    currentLocation = activeRunState.currentLocation,
                    locations = activeRunState.runData.locations,
                    onSnapshot = {},
                    modifier = Modifier.fillMaxSize()
                )
                
                RunDataCard(
                    elapsedTime = activeRunState.elapsedTime,
                    runData = activeRunState.runData,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(paddingValues)
                        .fillMaxWidth()
                )
            }
        }
    )

    if (activeRunState.shouldShowLocationPermissionRationale || activeRunState.shouldShowNotificationPermissionRationale) {
        BusbyRunnerDialog(
            title = stringResource(R.string.permission_required),
            onDismiss = { /** Normal dismissing is not allowed for permissions */ },
            description = when {
                activeRunState.shouldShowLocationPermissionRationale && activeRunState.shouldShowNotificationPermissionRationale -> {
                    stringResource(R.string.location_notification_rationale)
                }
                activeRunState.shouldShowLocationPermissionRationale -> {
                    stringResource(R.string.location_rationale)
                }
                else -> {
                    stringResource(R.string.notification_rationale)
                }
            },
            primaryButton = {
                BusbyRunnerOutlineActionButton(
                    text = stringResource(R.string.okay), isLoading = false,
                    onClicked = {
                        onActiveRunAction(ActiveRunAction.DismissRationalDialog)
                        permissionLauncher.requestBusbyRunnerPermissions(context)
                    }
                )
            })
    }
}

private fun ActivityResultLauncher<Array<String>>.requestBusbyRunnerPermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNoticationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val notificationPermission = if(Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else emptyArray()

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermission)
        }
        !hasLocationPermission -> {
            launch(locationPermissions)
        }
        !hasNotificationPermission -> {
            launch(notificationPermission)
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
fun PreviewActiveRunScreen() {
    BusbyRunnerTheme {
        ActiveRunScreen(
            activeRunState = ActiveRunState(
               runData = RunData(
                    distanceMeters = 6783,
                    pace = 3.minutes
                )
            ),
            onActiveRunAction = {},
            onServiceToggle = {}
        )
    }
}