package ru.rcfh.feature.templates.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.feature.templates.presentation.TemplatesViewModel

val TemplatesModule = module {
    viewModelOf(::TemplatesViewModel)
}