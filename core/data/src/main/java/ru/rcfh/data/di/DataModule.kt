package ru.rcfh.data.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.rcfh.data.network.AndroidNetworkMonitor
import ru.rcfh.data.network.NetworkMonitor
import ru.rcfh.data.repository.HandbookRepository

val DataModule = module {
    singleOf(::AndroidNetworkMonitor) bind NetworkMonitor::class
    singleOf(::HandbookRepository)
}