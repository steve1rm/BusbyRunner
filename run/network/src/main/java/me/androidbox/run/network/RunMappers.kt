package me.androidbox.run.network

import me.androidbox.core.domain.location.Latitude
import me.androidbox.core.domain.location.Location
import me.androidbox.core.domain.location.Longitude
import me.androidbox.core.domain.run.RunModel
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRunModel(): RunModel {
    return RunModel(
        id = this.id,
        duration = this.durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(this.dateTimeUtc).atZone(ZoneOffset.UTC),
        distanceMeters = this.distanceMeters,
        location = Location(Latitude(this.lat), Longitude(this.long)),
        maxSpeedKmh = this.maxSpeedKmh,
        totalElevationMeters = this.totalElevationMeters,
        mapPictureUrl = this.mapPictureUrl
    )
}

fun RunModel.toCreateRunRequest(): CreateRunRequest {
    return CreateRunRequest(
        id = this.id!!,
        durationMillis = this.duration.inWholeMilliseconds,
        distanceMeters = this.distanceMeters,
        lat = this.location.latitude.value,
        long = this.location.longitude.value,
        avgSpeedKmh = this.avgSpeedKmh,
        maxSpeedKmh = this.maxSpeedKmh,
        totalElevationMeters = this.totalElevationMeters,
        epochMillis = dateTimeUtc.toEpochSecond() * 1_000L
    )
}