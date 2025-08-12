package ru.rcfh.data.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.rcfh.data.handbook.FileHandbookManager
import ru.rcfh.data.handbook.HandbookManager
import ru.rcfh.data.network.AndroidNetworkMonitor
import ru.rcfh.data.network.NetworkMonitor

val DataModule = module {
    singleOf(::AndroidNetworkMonitor) bind NetworkMonitor::class
    singleOf(::FileHandbookManager) bind HandbookManager::class
}