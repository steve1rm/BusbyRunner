package me.androidbox.core.presentation.ui

import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration

fun Duration.formatted(): String {
    val totalSeconds = this.inWholeSeconds
    val hours = String.format("%02d", totalSeconds / (60 * 60))
    val minutes = String.format("%02d", (totalSeconds % 3600) / 60)
    val seconds = String.format("%02d", (totalSeconds % 60))

    return "$hours:$minutes:$seconds"
}

fun Double.toFormattedKm(): String {
    return "${this.roundToDecimals(1)} km"
}

fun Duration.toFormattedPace(distanceKm: Double): String {
    return if(this == Duration.ZERO || distanceKm <= 0.0) {
        "-"
    }
    else {
        val secondsPerKm = (this.inWholeSeconds / distanceKm).roundToInt()
        val avgPaceMinutes = secondsPerKm / 60
        val avgPaceSeconds = String.format("%02d", secondsPerKm % 60)

        "$avgPaceMinutes:$avgPaceSeconds / km"
    }
}

/** 5.678
 *  factor = 10^1 = 10
 *  5.7 */
private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)

    return round(this * factor) / factor
}