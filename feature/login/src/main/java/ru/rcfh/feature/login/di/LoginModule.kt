package ru.rcfh.feature.login.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.feature.login.presentation.LoginViewModel

val LoginModule = module {
    viewModelOf(::LoginViewModel)
}