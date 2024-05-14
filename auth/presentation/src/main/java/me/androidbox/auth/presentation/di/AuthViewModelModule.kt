package me.androidbox.auth.presentation.di

import me.androidbox.auth.presentation.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authViewModelModule = module {
/*
`   Another way of doing this
    viewModel<RegisterViewModel> {
        RegisterViewModel(get<UserDataValidator>())
    }*/

    viewModelOf(::RegisterViewModel)
}