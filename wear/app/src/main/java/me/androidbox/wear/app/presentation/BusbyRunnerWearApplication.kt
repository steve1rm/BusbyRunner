package me.androidbox.wear.app.presentation

import android.app.Application
import me.androidbox.wear.run.presentation.di.trackerScreenModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BusbyRunnerWearApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BusbyRunnerWearApplication)
            modules(
                trackerScreenModule
            )
        }
    }
}