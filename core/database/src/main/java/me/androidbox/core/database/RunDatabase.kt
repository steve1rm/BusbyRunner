package me.androidbox.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.androidbox.core.database.dao.RunDao
import me.androidbox.core.database.dao.RunPendingSyncDao
import me.androidbox.core.database.entity.DeletedRunSyncEntity
import me.androidbox.core.database.entity.RunEntity
import me.androidbox.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao

    abstract val runPendingSyncDao: RunPendingSyncDao
}