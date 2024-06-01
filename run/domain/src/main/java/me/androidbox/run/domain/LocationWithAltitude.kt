package me.androidbox.run.domain

import me.androidbox.core.domain.location.Location

data class LocationWithAltitude(
    val location: Location = Location(),
    val altitude: Double = 0.0
)
