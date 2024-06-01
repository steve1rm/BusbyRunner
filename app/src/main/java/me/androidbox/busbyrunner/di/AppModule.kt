package me.androidbox.busbyrunner.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.CoroutineScope
import me.androidbox.busbyrunner.BusbyRunnerApplication
import me.androidbox.busbyrunner.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single<SharedPreferences> {
        EncryptedSharedPreferences(
            androidApplication(),
            "secret_shared_prefs",
            MasterKey(androidApplication())
        )
    }

    viewModelOf(::MainViewModel)

    single<CoroutineScope> {
        (androidApplication() as BusbyRunnerApplication).applicationScope
    }
}