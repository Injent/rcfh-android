package ru.rcfh.feature.login.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.feature.login.presentation.signin.SignInViewModel
import ru.rcfh.feature.login.presentation.signup.SignUpViewModel

val LoginModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
}