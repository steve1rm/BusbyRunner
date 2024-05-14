package me.androidbox.auth.data.di

import me.androidbox.auth.data.AuthorizationRepositoryImp
import me.androidbox.auth.data.EmailPatternValidatorImp
import me.androidbox.auth.domain.AuthorizationRepository
import me.androidbox.auth.domain.PatternValidator
import me.androidbox.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidatorImp
    }

    /*
       Another way to do this
       single {
           UserDataValidator(get())
       }*/

    singleOf(::UserDataValidator)
    singleOf(::AuthorizationRepositoryImp).bind<AuthorizationRepository>()
}