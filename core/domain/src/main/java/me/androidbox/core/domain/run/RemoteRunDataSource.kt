package me.androidbox.core.domain.run

import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.EmptyResult
import me.androidbox.core.domain.util.Result

interface RemoteRunDataSource {

    suspend fun getRuns(): Result<List<RunModel>, DataError.Network>

    suspend fun postRun(runModel: RunModel, mapPicture: ByteArray): Result<RunModel, DataError.Network>

    suspend fun deleteRun(id: String): EmptyResult<DataError.Network>
}