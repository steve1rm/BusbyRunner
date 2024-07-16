package me.androidbox.wear.run.data.di

import me.androidbox.wear.run.data.HealthExerciseTrackerImp
import me.androidbox.wear.run.domain.ExerciseTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val wearRunDataModule = module {

    singleOf(::HealthExerciseTrackerImp).bind<ExerciseTracker>()
}