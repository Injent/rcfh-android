package ru.rcfh.core.sdui.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.rcfh.core.sdui.repository.FormRepository
import ru.rcfh.core.sdui.storage.StateCacheStorage

val SduiModule = module {
    singleOf(::StateCacheStorage)
    singleOf(::FormRepository)
}