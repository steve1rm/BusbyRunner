package me.androidbox.wear.run.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.FilledTonalIconButton
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.ExclamationMarkIcon
import me.androidbox.core.presentation.designsystem.FinishIcon
import me.androidbox.core.presentation.designsystem.PauseIcon
import me.androidbox.core.presentation.designsystem.StartIcon
import me.androidbox.core.presentation.ui.formatted
import me.androidbox.core.presentation.ui.toFormattedHeartRate
import me.androidbox.core.presentation.ui.toFormattedKm
import me.androidbox.wear.run.presentation.ambient.AmbientObserver
import me.androidbox.wear.run.presentation.ambient.ambientMode
import me.androidbox.wear.run.presentation.components.RunDataCard

@Composable
fun TrackerScreen(
    trackerAction: (trackerAction: TrackerAction) -> Unit,
    trackerState: TrackerState
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { mapOfPermissions ->
        val hasBodySensorPermissions = mapOfPermissions[Manifest.permission.BODY_SENSORS] == true

        trackerAction(TrackerAction.OnBodySensorPermissionResult(hasBodySensorPermissions))
    }

    LaunchedEffect(key1 = true) {
        val hasBodySensorPermission = context.checkSelfPermission(Manifest.permission.BODY_SENSORS) ==
                PackageManager.PERMISSION_GRANTED

        val hasNotificationPermission = if(Build.VERSION.SDK_INT >= 33) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        } else {
            true
        }

        val listOfPermission = mutableListOf<String>()

        if(!hasNotificationPermission && Build.VERSION.SDK_INT >= 33) {
            listOfPermission.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if(!hasBodySensorPermission) {
            listOfPermission.add(Manifest.permission.BODY_SENSORS)
        }

        permissionLauncher.launch(listOfPermission.toTypedArray())
    }

    AmbientObserver(
        onEnterAmbient = { ambientDetails ->
            trackerAction(TrackerAction.OnEnterAmbientMode(
                burnInProtectionRequired = ambientDetails.burnInProtectionRequired
            ))
        },
        onExitAmbient = {
            trackerAction(TrackerAction.OnExitAmbientMode)
        })

    if(trackerState.isConnectedPhoneNearBy) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .ambientMode(trackerState.isAmbientMode, trackerState.burnInProtectionRequired),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                RunDataCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.heart_rate),
                    value = if(trackerState.canTrackHeartRate) { trackerState.heartRate.toFormattedHeartRate() }
                    else {
                        stringResource(R.string.unsupported_heart_rate)
                    },
                    valueTextColor = if(trackerState.canTrackHeartRate) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                RunDataCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.distance),
                    value = (trackerState.distanceMeters / 1000.0).toFormattedKm()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary),
                text = trackerState.elapsedDuration.formatted(),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(trackerState.isTrackable) {
                    ToggleRunButton(
                        trackerState.isRunActive,
                        onClicked = {
                            trackerAction(TrackerAction.OnToggleRunClicked)
                        }
                    )

                    if(!trackerState.isRunActive && trackerState.hasStartedRunning) {
                        FilledTonalIconButton(
                            onClick = {
                                trackerAction(TrackerAction.OnFinishRunClicked)
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Icon(
                                imageVector = FinishIcon,
                                contentDescription = stringResource(R.string.stop_run),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                else {
                    Text(
                        text = stringResource(R.string.get_started),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
    else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ExclamationMarkIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.unconnected_message),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ToggleRunButton(
    isRunActive: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedIconButton(
        onClick = onClicked,
        modifier = modifier
    ) {
        if (isRunActive) {
            Icon(
                imageVector = PauseIcon,
                contentDescription = stringResource(R.string.pause_active_run),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        else {
            Icon(
                imageVector = StartIcon,
                contentDescription = stringResource(R.string.start_run),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
@WearPreviewDevices
fun PreviewTrackerScreen() {
    BusbyRunnerTheme {
        TrackerScreen(
            trackerState = TrackerState(
                isConnectedPhoneNearBy = true,
                isTrackable = true,
                isRunActive = false,
                canTrackHeartRate = true,
                hasStartedRunning = true,
                heartRate = 162),
            trackerAction = {}
        )
    }
}