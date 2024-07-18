package me.androidbox.wear.app.presentation.di

import kotlinx.coroutines.CoroutineScope
import me.androidbox.wear.app.presentation.BusbyRunnerWearApplication
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {

    single<CoroutineScope> {
        (androidApplication() as BusbyRunnerWearApplication).applicationScope
    }
}