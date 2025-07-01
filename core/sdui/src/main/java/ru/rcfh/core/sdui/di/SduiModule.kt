package ru.rcfh.core.sdui.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.rcfh.core.sdui.data.DocumentRepository
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.core.sdui.data.FormRepo
import ru.rcfh.core.sdui.data.FormStateManager

val SduiModule = module {
    singleOf(::FormRepo)
    singleOf(::FormStateManager)
    singleOf(::DocumentStateManager)
    singleOf(::DocumentRepository)
}