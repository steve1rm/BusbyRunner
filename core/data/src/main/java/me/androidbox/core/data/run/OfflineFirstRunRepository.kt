package me.androidbox.core.data.run

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
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
    private val applicationScope: CoroutineScope
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

        applicationScope.async {
            val result = remoteRunDataSource.deleteRun(id)
        }.await()

    }
}