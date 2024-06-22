package me.androidbox.run.data.scheduler

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.androidbox.core.database.dao.RunPendingSyncDao
import me.androidbox.core.database.entity.DeletedRunSyncEntity
import me.androidbox.core.database.entity.RunPendingSyncEntity
import me.androidbox.core.database.mappers.toRunEntity
import me.androidbox.core.domain.SessionStorage
import me.androidbox.core.domain.run.RunId
import me.androidbox.core.domain.run.RunModel
import me.androidbox.run.data.workers.CreateRunWorker
import me.androidbox.run.data.workers.DeleteRunWorker
import me.androidbox.run.data.workers.FetchRunsWorker
import me.androidbox.core.domain.run.SyncRunScheduler
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerSchedulerImp(
    private val context: Context,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(syncType: SyncRunScheduler.SyncType) {
        when(syncType) {
            is SyncRunScheduler.SyncType.CreateRun -> {
                scheduledCreateRunsWorker(syncType.runModel, syncType.mapPictureBytes)
            }
            is SyncRunScheduler.SyncType.DeleteRun -> {
                scheduleDeleteRunsWork(syncType.runId)
            }
            is SyncRunScheduler.SyncType.FetchRuns -> {
                scheduledFetchRunsWorker(syncType.interval)
            }
        }
    }

    override suspend fun cancelAllSyncs() {
        workManager
            .cancelAllWork()
            .await()
    }

    private suspend fun scheduledCreateRunsWorker(runModel: RunModel, mapPictureBytes: ByteArray) {
        sessionStorage.get()?.let { authorization ->
            val userid = authorization.userId

            val runPendingSyncEntity = RunPendingSyncEntity(
                userId = userid,
                run = runModel.toRunEntity(),
                mapPictureBytes = mapPictureBytes
            )

            runPendingSyncDao.upsertRunPendingSyncEntity(runPendingSyncEntity)

            val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(networkType = NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.EXPONENTIAL,
                    backoffDelay = 2_000L,
                    timeUnit = TimeUnit.MILLISECONDS
                )
                .setInitialDelay(
                    duration = 30,
                    timeUnit = TimeUnit.MINUTES
                )
                .setInputData(
                    Data.Builder()
                        .putString(
                            CreateRunWorker.RUN_ID,
                            runPendingSyncEntity.runId)
                        .build()
                )
                .addTag("create_work")
                .build()

            applicationScope.launch {
                workManager.enqueue(workRequest)
            }.join()
        }
    }

    private suspend fun scheduleDeleteRunsWork(runId: RunId) {
        sessionStorage.get()?.let { authorizationInfo ->
            val entity = DeletedRunSyncEntity(
                runId = runId,
                userId = authorizationInfo.userId
            )

            runPendingSyncDao.upsertDeletedRunSyncEntity(entity)

            val workRequest = OneTimeWorkRequestBuilder<DeleteRunWorker>()
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(networkType = NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.EXPONENTIAL,
                    backoffDelay = 2_000L,
                    timeUnit = TimeUnit.MILLISECONDS
                )
                .setInitialDelay(
                    duration = 30,
                    timeUnit = TimeUnit.MINUTES
                )
                .setInputData(
                    Data.Builder()
                        .putString(
                            DeleteRunWorker.RUN_ID,
                            entity.runId)
                        .build()
                )
                .addTag("delete_work")
                .build()

            applicationScope.launch {
                workManager.enqueue(workRequest)
            }.join()
        }
    }

    private suspend fun scheduledFetchRunsWorker(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager
                .getWorkInfosByTag("sync_work")
                .get()
                .isNotEmpty()
        }

        if (!isSyncScheduled) {
            val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(
                repeatInterval = interval.toJavaDuration()
            )
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(networkType = NetworkType.CONNECTED)
                    .build()
                )
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.EXPONENTIAL,
                    backoffDelay = 2_000L,
                    timeUnit = TimeUnit.MILLISECONDS
                )
                .setInitialDelay(
                    duration = 30,
                    timeUnit = TimeUnit.MINUTES
                )
                .addTag("sync_work")
                .build()

            workManager.enqueue(workRequest)
        }
    }
}