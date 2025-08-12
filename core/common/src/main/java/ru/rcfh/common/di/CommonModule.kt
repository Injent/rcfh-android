package ru.rcfh.common.di

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import ru.rcfh.common.AppDispatchers

val CommonModule = module {
    single {
        AppDispatchers(
            ioDispatcher = Dispatchers.IO,
            defaultDispatcher = Dispatchers.Default
        )
    }
}