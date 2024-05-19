package me.androidbox.run.location.di

import me.androidbox.run.domain.LocationObserver
import me.androidbox.run.location.AndroidLocationObserverImp
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {

    singleOf(::AndroidLocationObserverImp).bind<LocationObserver>()
}