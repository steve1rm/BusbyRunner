package me.androidbox.run.location

import android.location.Location
import me.androidbox.core.domain.location.Latitude
import me.androidbox.core.domain.location.Longitude
import me.androidbox.run.domain.LocationWithAltitude

fun Location.toLocationWithAltitude(): LocationWithAltitude {
    return LocationWithAltitude(
        location = me.androidbox.core.domain.location.Location(
            latitude = Latitude(value = this.latitude),
            longitude = Longitude(value = this.longitude),
        ),
        altitude = this.altitude
    )
}
