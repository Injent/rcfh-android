package ru.rcfh.core.sync.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.rcfh.core.sync.work.AndroidSyncManager
import ru.rcfh.data.util.SyncManager

val SyncModule = module {
    singleOf(::AndroidSyncManager) bind SyncManager::class
}