package me.androidbox.core.domain.run

import kotlinx.coroutines.flow.Flow
import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.Result

typealias RunId = String

interface LocalRunDataSource {

    fun getRuns(): Flow<List<RunModel>>

    suspend fun upsertRun(runModel: RunModel): Result<RunId, DataError.Local>

    suspend fun upsertListOfRuns(runModel: List<RunModel>): Result<List<RunId>, DataError.Local>

    suspend fun deleteRun(id: String)

    suspend fun deleteAllRuns()
}