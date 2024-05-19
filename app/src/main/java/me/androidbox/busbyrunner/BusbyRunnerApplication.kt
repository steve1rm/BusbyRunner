package me.androidbox.busbyrunner

import android.app.Application
import me.androidbox.auth.data.di.authDataModule
import me.androidbox.auth.presentation.di.authViewModelModule
import me.androidbox.busbyrunner.di.appModule
import me.androidbox.core.data.di.coreDataModule
import me.androidbox.run.location.di.locationModule
import me.androidbox.run.presentation.di.runViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class BusbyRunnerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@BusbyRunnerApplication)
            modules(
                appModule,
                authViewModelModule,
                authDataModule,
                coreDataModule,
                runViewModelModule,
                locationModule
            )
        }
    }
}
