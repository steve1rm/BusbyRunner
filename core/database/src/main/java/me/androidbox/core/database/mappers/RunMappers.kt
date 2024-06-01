package me.androidbox.core.database.mappers

import me.androidbox.core.database.entity.RunEntity
import me.androidbox.core.domain.location.Latitude
import me.androidbox.core.domain.location.Location
import me.androidbox.core.domain.location.Longitude
import me.androidbox.core.domain.run.RunModel
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toRunModel(): RunModel {
    return RunModel(
        id = this.id,
        duration = this.durationMs.milliseconds,
        dateTimeUtc = Instant.parse(this.dateTimeUtc)
            .atZone(ZoneOffset.UTC),
        distanceMeters = this.distanceMeters,
        location = Location(
            latitude = Latitude(this.latitude),
            longitude = Longitude(this.longitude)
        ),
        maxSpeedKmh = this.maxSpeedKmh,
        totalElevationMeters = this.totalElevationMeters,
        mapPictureUrl = this.mapPictureUrl
    )
}

fun RunModel.toRunEntity(): RunEntity {
    return RunEntity(
        id = this.id ?: ObjectId().toHexString(),
        durationMs = this.duration.inWholeMilliseconds,
        dateTimeUtc = this.dateTimeUtc.toInstant().toString(),
        distanceMeters = this.distanceMeters,
        latitude = this.location.latitude.value,
        longitude = this.location.longitude.value,
        avgSpeedKmh = this.avgSpeedKmh,
        maxSpeedKmh = this.maxSpeedKmh,
        totalElevationMeters = this.totalElevationMeters,
        mapPictureUrl = this.mapPictureUrl
    )
}

