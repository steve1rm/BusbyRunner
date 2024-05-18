package me.androidbox.run.presentation.di

import me.androidbox.run.presentation.active_run.ActiveRunViewModel
import me.androidbox.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val runViewModelModule = module {

    viewModelOf(::RunOverviewViewModel)

    viewModelOf<ActiveRunViewModel>(::ActiveRunViewModel)
}