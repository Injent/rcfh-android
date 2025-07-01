package ru.rcfh.feature.settings.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.feature.settings.presentation.SettingsViewModel

val SettingsModule = module {
    viewModelOf(::SettingsViewModel)
}