package me.androidbox.wear.app.presentation

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.androidbox.core.connectivity.data.di.coreConnectivityDataModule
import me.androidbox.wear.app.presentation.di.appModule
import me.androidbox.wear.run.data.di.wearRunDataModule
import me.androidbox.wear.run.presentation.di.wearRunPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BusbyRunnerWearApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@BusbyRunnerWearApplication)
            modules(
                appModule,
                wearRunPresentationModule,
                wearRunDataModule,
                coreConnectivityDataModule
            )
        }
    }
}