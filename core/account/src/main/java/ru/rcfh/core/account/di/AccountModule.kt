package ru.rcfh.core.account.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.rcfh.core.account.repository.AccountRepository
import ru.rcfh.core.account.util.RcfhAccountManager

val AccountModule = module {
    singleOf(::RcfhAccountManager)
    singleOf(::AccountRepository)
}