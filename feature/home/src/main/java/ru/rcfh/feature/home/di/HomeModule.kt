package ru.rcfh.feature.home.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.feature.home.presentation.HomeViewModel

val HomeModule = module {
    viewModelOf(::HomeViewModel)
}