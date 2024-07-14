package me.androidbox.anlaytics.presentation

import me.androidbox.analytics.domain.AnalyticsValues
import me.androidbox.core.presentation.ui.formatted
import me.androidbox.core.presentation.ui.toFormattedKm
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

fun Duration.toFormattedTotalTime(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60

    return "${days}d ${hours}h ${minutes}m"
}

fun AnalyticsValues.toAnalyticsDashboardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistanceRun = (this.totalDistanceRun / 1000.0).toFormattedKm(),
        totalTimeRun = this.totalTimeRun.toFormattedTotalTime(),
        fastestEverRun = this.fastestEverRun.toFormattedKm(),
        avgDistance = (this.avgDistancePerRun / 1000.0).toFormattedKm(),
        avgPace = this.avgPacePerRun.seconds.formatted()
    )
}