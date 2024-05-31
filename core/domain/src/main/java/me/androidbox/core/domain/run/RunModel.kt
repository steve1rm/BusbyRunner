package me.androidbox.core.domain.run

import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

class RunModel(
    val id: String?, // null if new run
    val duration: Duration,
    val dateTimeUtc: ZonedDateTime,
    val distanceMeters: Int,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?
) {

    val avgSpeedKmh: Double
        get() {
            return (distanceMeters / 1000.0) / duration.toDouble(DurationUnit.HOURS)
        }
}