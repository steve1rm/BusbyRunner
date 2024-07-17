package me.androidbox.wear.run.data.di

import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.HealthServices
import me.androidbox.wear.run.data.HealthExerciseTrackerImp
import me.androidbox.wear.run.domain.ExerciseTracker
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val wearRunDataModule = module {

    single<ExerciseClient> {
        HealthServices.getClient(androidContext()).exerciseClient
    }

    singleOf(::HealthExerciseTrackerImp).bind<ExerciseTracker>()
}