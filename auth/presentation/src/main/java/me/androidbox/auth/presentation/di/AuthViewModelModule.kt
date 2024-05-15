package me.androidbox.auth.presentation.di

import me.androidbox.auth.domain.AuthorizationRepository
import me.androidbox.auth.domain.UserDataValidator
import me.androidbox.auth.presentation.login.LoginViewModel
import me.androidbox.auth.presentation.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authViewModelModule = module {
/*
`   Another way of doing this
    viewModel<RegisterViewModel> {
        RegisterViewModel(get<UserDataValidator>())
    }*/

    viewModelOf(::RegisterViewModel)

    //viewModelOf(::LoginViewModel)
    viewModel<LoginViewModel> {
        LoginViewModel(get<AuthorizationRepository>(), get<UserDataValidator>())
    }
}