package me.androidbox.analytics.data.di

import me.androidbox.analytics.data.RoomAnalyticsRepositoryImp
import me.androidbox.analytics.domain.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsDataModule = module {

    singleOf(::RoomAnalyticsRepositoryImp).bind<AnalyticsRepository>()
}