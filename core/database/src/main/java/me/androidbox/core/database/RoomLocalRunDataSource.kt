package me.androidbox.core.database

import android.database.sqlite.SQLiteFullException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.androidbox.core.database.dao.RunDao
import me.androidbox.core.database.mappers.toRunEntity
import me.androidbox.core.database.mappers.toRunModel
import me.androidbox.core.domain.run.LocalRunDataSource
import me.androidbox.core.domain.run.RunId
import me.androidbox.core.domain.run.RunModel
import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.Result

class RoomLocalRunDataSource(
    private val runDao: RunDao
) : LocalRunDataSource {

    override fun getRuns(): Flow<List<RunModel>> {
        return runDao.getRuns().map { listOfRunEntity ->
            listOfRunEntity.map {
                it.toRunModel()
            }
        }
    }

    override suspend fun upsertRun(runModel: RunModel): Result<RunId, DataError.Local> {
        return try {
            val runEntity = runModel.toRunEntity()

            runDao.upsertRun(runEntity)

            Result.Success(runEntity.id)
        }
        catch(exception: SQLiteFullException) {
            return Result.Failure(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun upsertListOfRuns(runModel: List<RunModel>): Result<List<RunId>, DataError.Local> {
        return try {
            val listOfRunEntities = runModel.map { it.toRunEntity() }

            runDao.upsertListOfRuns(listOfRunEntities)

            Result.Success(listOfRunEntities.map { it.id })
        }
        catch (exception: SQLiteFullException) {
            Result.Failure(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteRun(id: String) {
        runDao.deleteRun(id)
    }

    override suspend fun deleteAllRuns() {
        runDao.deleteAllRuns()
    }
}