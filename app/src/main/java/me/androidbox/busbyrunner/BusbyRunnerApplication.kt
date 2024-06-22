package me.androidbox.busbyrunner

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import me.androidbox.auth.data.di.authDataModule
import me.androidbox.auth.presentation.di.authViewModelModule
import me.androidbox.busbyrunner.di.appModule
import me.androidbox.core.data.di.coreDataModule
import me.androidbox.core.database.di.databaseModule
import me.androidbox.run.data.di.runDataModule
import me.androidbox.run.location.di.locationModule
import me.androidbox.run.network.di.networkModule
import me.androidbox.run.presentation.di.runViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class BusbyRunnerApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@BusbyRunnerApplication)
            workManagerFactory()
            modules(
                appModule,
                authViewModelModule,
                authDataModule,
                coreDataModule,
                runViewModelModule,
                locationModule,
                databaseModule,
                networkModule,
                runDataModule
            )
        }
    }
}
