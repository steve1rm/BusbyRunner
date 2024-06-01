package me.androidbox.run.presentation.run_overview.mappers

import me.androidbox.core.domain.run.RunModel
import me.androidbox.core.presentation.ui.formatted
import me.androidbox.core.presentation.ui.toFormattedKm
import me.androidbox.core.presentation.ui.toFormattedMeters
import me.androidbox.core.presentation.ui.toFormattedPace
import me.androidbox.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun RunModel.toRunUi(): RunUi {
    val dataTimeInLocalTime = this.dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())

    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - hh:mma")
        .format(dataTimeInLocalTime)

    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = this.id!!,
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKm(),
        avgSpeed = this.avgSpeedKmh.toFormattedKm(),
        maxSpeed = this.maxSpeedKmh.toFormattedKm(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = this.mapPictureUrl
    )
}