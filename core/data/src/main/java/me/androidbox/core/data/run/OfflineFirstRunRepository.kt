package me.androidbox.core.data.run

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.androidbox.core.database.dao.RunPendingSyncDao
import me.androidbox.core.database.mappers.toRunModel
import me.androidbox.core.domain.SessionStorage
import me.androidbox.core.domain.run.LocalRunDataSource
import me.androidbox.core.domain.run.RemoteRunDataSource
import me.androidbox.core.domain.run.RunId
import me.androidbox.core.domain.run.RunModel
import me.androidbox.core.domain.run.RunRepository
import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.EmptyResult
import me.androidbox.core.domain.util.Result
import me.androidbox.core.domain.util.asEmptyResult

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage
) : RunRepository {

    override fun getRuns(): Flow<List<RunModel>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when(val result = remoteRunDataSource.getRuns()) {
            is Result.Failure -> {
                result.asEmptyResult()
            }
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertListOfRuns(result.data).asEmptyResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(runModel: RunModel, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(runModel)

        return if(localResult is Result.Success) {
            val runWithId = localResult.data
            val run = runModel.copy(id = runWithId)

            val remoteResult = remoteRunDataSource.postRun(
                runModel = run,
                mapPicture = mapPicture)

            return when(remoteResult) {
                is Result.Failure -> {
                    Result.Success(Unit)
                }
                is Result.Success -> {
                    applicationScope.async {
                        localRunDataSource.upsertRun(remoteResult.data).asEmptyResult()
                    }.await()
                }
            }
        }
        else {
            localResult.asEmptyResult()
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        /**
         * Section 9.7 19:50 delete run if its been deleted locally and hasn't been sync'ed with the BE
         * Edge case where the run was created offline and was deleted offline, so no need to sync with BE
         */
        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(runId = id)

        if (isPendingSync != null) {
            runPendingSyncDao.deleteRunPendingSyncEntity(runId = id)
        }
        else {
            applicationScope.async {
                remoteRunDataSource.deleteRun(id)
            }.await()
        }
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            sessionStorage.get()?.let { authorizationInfo ->
                val userId = authorizationInfo.userId

                /** Created locally but didn't sync with the BE */
                val createdRuns = async {
                    runPendingSyncDao.getAllRunPendingSyncEntities(userId = userId)
                }

                /** Deleted locally but didn't sync with the BE */
                val deletedRuns = async {
                    runPendingSyncDao.getAllDeletedRunSyncEntities(userId = userId)
                }

                val createdJobs = createdRuns
                    .await()
                    .map { runPendingSyncEntity ->
                        launch {
                            val runModel = runPendingSyncEntity.run.toRunModel()

                            when(remoteRunDataSource.postRun(
                                runModel = runModel,
                                mapPicture = runPendingSyncEntity.mapPictureBytes)) {

                                is Result.Failure -> {
                                    Unit
                                }
                                is Result.Success -> {
                                    applicationScope.launch {
                                        runPendingSyncDao.deleteRunPendingSyncEntity(
                                            runId = runPendingSyncEntity.runId)
                                    }.join()
                                }
                            }
                        }
                    }

                val deletedJobs = deletedRuns
                    .await()
                    .map { deletedRunSyncEntity ->
                        launch {

                            when(remoteRunDataSource.deleteRun(deletedRunSyncEntity.runId)) {
                                is Result.Failure -> {
                                    Unit
                                }
                                is Result.Success -> {
                                    applicationScope.launch {
                                        runPendingSyncDao.deleteDeletedRunSyncEntity(deletedRunSyncEntity.runId)
                                    }.join()
                                }
                            }
                        }
                    }

                createdJobs.forEach {
                    it.join()
                }
                deletedJobs.forEach {
                    it.join()
                }
            }
        }
    }
}