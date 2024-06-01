package me.androidbox.run.presentation.map

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import me.androidbox.run.domain.LocationTimestamp
import kotlin.math.abs

object PolylineColorCalculator {

    fun locationToColor(locationSrc: LocationTimestamp, locationDst: LocationTimestamp): Color {
        val distanceInMeters = locationSrc.location.location.distanceTo(locationDst.location.location)
        val timeDiff = abs((locationDst.durationTimestamps - locationSrc.durationTimestamps).inWholeSeconds)
        val speedKmh = (distanceInMeters / timeDiff) * 3.6

        return interpolateColor(
            speedKmh = speedKmh,
            minSpeed = 5.0,
            maxSpeed = 20.0,
            colorStart = Color.Green,
            colorMid = Color.Blue,
            colorEnd = Color.Red
        )
    }

    private fun interpolateColor(
        speedKmh: Double,
        minSpeed: Double,
        maxSpeed: Double,
        colorStart: Color,
        colorMid: Color,
        colorEnd: Color
    ) : Color {
        val ratio = ((speedKmh - minSpeed) / (maxSpeed - minSpeed)).coerceIn(0.0..1.0)
        val colorInt = if(ratio <= 0.5) {
            val midRatio = ratio / 0.5
            ColorUtils.blendARGB(colorStart.toArgb(), colorMid.toArgb(), midRatio.toFloat())
        }
        else {
            val midToEndRatio = (ratio - 0.5) / 0.5
            ColorUtils.blendARGB(colorMid.toArgb(), colorEnd.toArgb(), midToEndRatio.toFloat())
        }

        return Color(colorInt)
    }
}