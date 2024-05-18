package me.androidbox.core.domain.location

data class Location(
    val latitude: Latitude = Latitude(value = 0.0),
    val longitude: Longitude = Longitude(value = 0.0),
)

@JvmInline
value class Latitude(
    val value: Double
)

@JvmInline
value class Longitude(
    val value: Double
)

