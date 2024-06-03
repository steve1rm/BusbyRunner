package me.androidbox.run.domain

import kotlin.math.roundToInt
import kotlin.time.DurationUnit

object LocationDataCalculator {
    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>) : Int {
        return locations
            .sumOf { timestampsPerLine ->
                timestampsPerLine.zipWithNext { location1, location2 ->
                    location1.location.location.distanceTo(location2.location.location)
                }
                    .sum()
                    .roundToInt()
            }
    }

    fun getMaxSpeedKmh(locations: List<List<LocationTimestamp>>): Double {
        return locations.maxOf { list ->
            list.zipWithNext { start, end ->
                val distance = start.location.location.distanceTo(end.location.location)

                val hoursDifference =
                    (end.durationTimestamps - start.durationTimestamps).toDouble(DurationUnit.HOURS)

                if (hoursDifference == 0.0) {
                    0.0
                }
                else (distance / 1000.0) / hoursDifference
            }.maxOrNull() ?: 0.0
        }
    }

    fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { list ->
            list.zipWithNext { start, end ->
                val altitudeStart = start.location.altitude
                val altitudeEnd = end.location.altitude
                (altitudeEnd - altitudeStart).coerceAtLeast(0.0)
            }
                .sum()
                .roundToInt()
        }
    }
}