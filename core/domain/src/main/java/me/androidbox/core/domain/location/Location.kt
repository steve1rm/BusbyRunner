package me.androidbox.core.domain.location

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.atan2

data class Location(
    val latitude: Latitude = Latitude(value = 0.0),
    val longitude: Longitude = Longitude(value = 0.0),
) {
    companion object {
        private const val EARTH_RADIUS_METERS = 6_371_000
    }

    fun distanceTo(other: Location): Float {
        val latDistance = Math.toRadians(other.latitude.value - latitude.value)
        val longDistance = Math.toRadians(other.longitude.value - longitude.value)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(latitude.value)) * cos(Math.toRadians(other.latitude.value)) *
                sin(longDistance / 2) * sin(longDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c.toFloat()
    }

}

@JvmInline
value class Latitude(
    val value: Double
)

@JvmInline
value class Longitude(
    val value: Double
)

