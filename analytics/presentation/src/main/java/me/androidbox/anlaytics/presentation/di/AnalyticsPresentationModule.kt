package me.androidbox.anlaytics.presentation.di

import me.androidbox.anlaytics.presentation.AnalyticsDashboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val analyticPresentationModule = module {
    viewModelOf(::AnalyticsDashboardViewModel)
}