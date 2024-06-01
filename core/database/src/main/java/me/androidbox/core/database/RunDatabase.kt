package me.androidbox.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.androidbox.core.database.dao.RunDao
import me.androidbox.core.database.entity.RunEntity

@Database(
    entities = [RunEntity::class],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao
}