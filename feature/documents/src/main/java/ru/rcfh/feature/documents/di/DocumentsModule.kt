package ru.rcfh.feature.documents.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.feature.documents.presentation.details.DocumentViewModel
import ru.rcfh.feature.documents.presentation.list.DocumentListViewModel

val DocumentsModule = module {
    viewModelOf(::DocumentListViewModel)
    viewModelOf(::DocumentViewModel)
    //viewModelOf(::SummarizeViewModel)
}