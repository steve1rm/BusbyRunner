package me.androidbox.run.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.run.domain.LocationTimestamp

@Composable
fun BusbyPolylines(locations: List<List<LocationTimestamp>>) {
    val polylines = remember(key1 = locations) {
        locations.map { listOfTimestamps ->
            listOfTimestamps.zipWithNext { timestampSrc, timestampDst ->
                PolylineUi(
                    locationSrc = timestampSrc.location.location,
                    locationDst = timestampDst.location.location,
                    color = PolylineColorCalculator.locationToColor(
                        timestampSrc,
                        timestampDst
                    )
                )
            }
        }
    }

    polylines.forEach { polyline ->
        polyline.forEach { polylineUi ->
            Polyline(
                points = listOf(
                    LatLng(
                        polylineUi.locationSrc.latitude.value,
                        polylineUi.locationSrc.longitude.value
                    ),
                    LatLng(
                        polylineUi.locationDst.latitude.value,
                        polylineUi.locationDst.longitude.value
                    )
                ),
                color = polylineUi.color,
                jointType = JointType.BEVEL
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun Preview() {
    BusbyRunnerTheme {
        BusbyPolylines(locations = emptyList())
    }
}