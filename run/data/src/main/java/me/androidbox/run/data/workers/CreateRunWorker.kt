package me.androidbox.run.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.androidbox.core.database.dao.RunPendingSyncDao
import me.androidbox.core.database.mappers.toRunModel
import me.androidbox.core.domain.run.RemoteRunDataSource

class CreateRunWorker(
    context: Context,
    private val workerParameters: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val runPendingSyncDao: RunPendingSyncDao
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val RUN_ID = "run_id"
    }

    override suspend fun doWork(): Result {
        return if(runAttemptCount >= 5) {
            Result.failure()
        }
        else {
            workerParameters.inputData.getString(RUN_ID)?.let { runId ->
                runPendingSyncDao.getRunPendingSyncEntity(runId)?.let { runPendingSyncEntity ->
                    val runModel = runPendingSyncEntity.run.toRunModel()

                    when(val result = remoteRunDataSource.postRun(runModel, runPendingSyncEntity.mapPictureBytes)) {
                        is me.androidbox.core.domain.util.Result.Failure -> {
                            result.error.toWorkerResult()
                        }
                        is me.androidbox.core.domain.util.Result.Success -> {
                            runPendingSyncDao.deleteRunPendingSyncEntity(runId)
                            Result.Success()
                        }
                    }
                } ?: Result.failure()

            } ?: Result.failure()
        }
    }
}