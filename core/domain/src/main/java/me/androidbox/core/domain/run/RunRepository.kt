package me.androidbox.core.domain.run

import kotlinx.coroutines.flow.Flow
import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.EmptyResult

interface RunRepository {

    fun getRuns(): Flow<List<RunModel>>

    suspend fun fetchRuns(): EmptyResult<DataError>

    suspend fun upsertRun(run: RunModel, mapPicture: ByteArray): EmptyResult<DataError>

    suspend fun deleteRun(id: RunId)
}