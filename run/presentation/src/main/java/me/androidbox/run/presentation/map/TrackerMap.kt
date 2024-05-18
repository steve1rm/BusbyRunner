package me.androidbox.run.presentation.map

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import me.androidbox.core.domain.location.Location
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.RunIcon
import me.androidbox.run.domain.LocationTimestamp
import me.androidbox.run.presentation.R

@Composable
fun TrackerMap(
    isRunFinished: Boolean,
    currentLocation: Location?,
    locations: List<List<LocationTimestamp>>,
    onSnapshot: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()

    val markerPositionLatitude by animateFloatAsState(
        targetValue = currentLocation?.latitude?.value?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = "busbyRunner"
    )

    val markerPositionLongitude by animateFloatAsState(
        targetValue = currentLocation?.longitude?.value?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = "BusbyRunner"
    )

    val markerPosition = remember(key1 = markerPositionLatitude, key2 = markerPositionLongitude) {
        LatLng(markerPositionLatitude.toDouble(), markerPositionLongitude.toDouble())
    }

    LaunchedEffect(key1 = markerPosition, key2 = isRunFinished) {
        if(!isRunFinished) {
            markerState.position = markerPosition
        }
    }

    LaunchedEffect(key1 = currentLocation, key2 = isRunFinished) {
        if(currentLocation != null && !isRunFinished) {
            val latLng = LatLng(currentLocation.latitude.value, currentLocation.longitude.value)

            cameraPositionState.animate(update = CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        }
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = mapStyle
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false
        )
    ) {
        if (!isRunFinished && currentLocation != null) {
            MarkerComposable(
                currentLocation,
                state = markerState
            ) {
                Box(modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RunIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewTrackerMap() {
    BusbyRunnerTheme {
        TrackerMap(
            isRunFinished = false,
            currentLocation = null,
            locations = listOf(),
            onSnapshot = {}
        )
    }
}