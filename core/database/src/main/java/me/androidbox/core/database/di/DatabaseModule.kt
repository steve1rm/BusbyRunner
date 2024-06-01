package me.androidbox.core.database.di

import androidx.room.Room
import me.androidbox.core.database.RoomLocalRunDataSource
import me.androidbox.core.database.RunDatabase
import me.androidbox.core.database.dao.RunDao
import me.androidbox.core.domain.run.LocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            "run.db"
        ).build()
    }

    single {
        get<RunDatabase>().runDao
    }

    single<LocalRunDataSource> {
        RoomLocalRunDataSource(get<RunDao>())
    }
}
