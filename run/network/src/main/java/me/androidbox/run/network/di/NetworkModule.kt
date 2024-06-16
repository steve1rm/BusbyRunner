package me.androidbox.run.network.di

import me.androidbox.core.domain.run.RemoteRunDataSource
import me.androidbox.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {

    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()

}