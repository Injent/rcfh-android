package ru.rcfh.blank.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.rcfh.blank.presentation.blank.BlankViewModel
import ru.rcfh.blank.presentation.referencesearch.ReferenceSearchViewModel
import ru.rcfh.blank.presentation.viewer.ViewerViewModel
import ru.rcfh.data.repository.DocumentRepository
import ru.rcfh.data.repository.DocumentRepositoryImpl

val BlankModule = module {
    singleOf(::DocumentRepositoryImpl) bind DocumentRepository::class
    viewModelOf(::ViewerViewModel)
    viewModelOf(::BlankViewModel)
    viewModelOf(::ReferenceSearchViewModel)
}