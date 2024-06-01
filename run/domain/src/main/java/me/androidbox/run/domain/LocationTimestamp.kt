package me.androidbox.run.domain

import kotlin.time.Duration

data class LocationTimestamp(
    val location: LocationWithAltitude = LocationWithAltitude(),
    val durationTimestamps: Duration = Duration.ZERO
)
