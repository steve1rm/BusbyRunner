package me.androidbox.core.data.di

import me.androidbox.core.data.auth.EncryptedSessionStorageImp
import me.androidbox.core.data.networking.HttpClientFactory
import me.androidbox.core.data.run.OfflineFirstRunRepository
import me.androidbox.core.domain.SessionStorage
import me.androidbox.core.domain.run.RunRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get<SessionStorage>()).build()
    }

    /*
    Another way
    single<SessionStorage> {
        EncryptedSessionStorageImp(get<SharedPreferences>())
    }*/

    singleOf(::EncryptedSessionStorageImp).bind<SessionStorage>()

    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}