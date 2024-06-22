package me.androidbox.run.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.androidbox.core.domain.run.RunRepository

class FetchRunsWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val runRepository: RunRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        return if(runAttemptCount >= 5) {
            Result.failure()
        }
        else {
            when (val result = runRepository.fetchRuns()) {
                is me.androidbox.core.domain.util.Result.Failure -> {
                    result.error.toWorkerResult()
                }

                is me.androidbox.core.domain.util.Result.Success -> {
                    Result.success()
                }
            }
        }
    }
}