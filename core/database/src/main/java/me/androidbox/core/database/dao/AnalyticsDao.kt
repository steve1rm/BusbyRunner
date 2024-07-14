package me.androidbox.core.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AnalyticsDao {

    @Query("SELECT SUM(distanceMeters) FROM runentity")
    suspend fun getTotalDistance(): Int

    @Query("SELECT SUM(durationMs) FROM runentity")
    suspend fun getTotalTimeRun(): Long

    @Query("SELECT MAX(maxSpeedKmh) FROM runentity")
    suspend fun getMaxRunSpeed(): Double

    @Query("SELECT AVG(distanceMeters) FROM runentity")
    suspend fun getAvgDistancePerRun(): Double

    @Query("SELECT AVG(durationMs / 60000.0) / 10000.0 FROM runentity")
    suspend fun getAvgPacePerRun(): Double
}