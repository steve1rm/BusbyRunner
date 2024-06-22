package me.androidbox.core.domain.run

import kotlin.time.Duration

interface SyncRunScheduler {

    suspend fun scheduleSync(syncType: SyncType)
    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        /** Periodic for scheduling runs based on the duration to always run */
        data class FetchRuns(val interval: Duration) : SyncType

        /** On demand scheduling for deleting and creating when failed to sync remotely */
        data class DeleteRun(val runId: String) : SyncType

        data class CreateRun(val runModel: RunModel, val mapPictureBytes: ByteArray) : SyncType
    }
}