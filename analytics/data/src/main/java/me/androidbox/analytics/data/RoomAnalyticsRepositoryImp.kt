package me.androidbox.analytics.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import me.androidbox.analytics.domain.AnalyticsRepository
import me.androidbox.analytics.domain.AnalyticsValues
import me.androidbox.core.database.dao.AnalyticsDao
import kotlin.time.Duration.Companion.milliseconds

class RoomAnalyticsRepositoryImp(
    private val analyticsDao: AnalyticsDao
) : AnalyticsRepository {

    override suspend fun getAnalyticsValues(): AnalyticsValues {
        return withContext(Dispatchers.IO) {
            val totalDistanceRun = async { analyticsDao.getTotalDistance() }
            val totalTimeRun = async { analyticsDao.getTotalTimeRun() }
            val fastestEverRun = async { analyticsDao.getMaxRunSpeed() }
            val avgDistancePerRun = async { analyticsDao.getAvgDistancePerRun() }
            val avgPacePerRun = async { analyticsDao.getAvgPacePerRun() }

            AnalyticsValues(
                totalDistanceRun = totalDistanceRun.await(),
                totalTimeRun = totalTimeRun.await().milliseconds,
                fastestEverRun = fastestEverRun.await(),
                avgDistancePerRun = avgDistancePerRun.await(),
                avgPacePerRun = avgPacePerRun.await()
            )
        }
    }
}