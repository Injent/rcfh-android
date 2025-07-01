package ru.rcfh.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.MainActivityViewModel
import ru.rcfh.common.di.CommonModule
import ru.rcfh.core.account.di.AccountModule
import ru.rcfh.core.sdui.di.SduiModule
import ru.rcfh.core.sync.di.SyncModule
import ru.rcfh.data.di.DataModule
import ru.rcfh.database.di.DatabaseModule
import ru.rcfh.datastore.di.DataStoreModule
import ru.rcfh.feature.documents.di.DocumentsModule
import ru.rcfh.feature.home.di.HomeModule
import ru.rcfh.feature.login.di.LoginModule
import ru.rcfh.feature.settings.di.SettingsModule
import ru.rcfh.glpm.feature.form.di.FormModule
import ru.rcfh.network.di.NetworkModule

val AppModule = module {
    includes(
        CommonModule,
        NetworkModule,
        DataStoreModule,
        DatabaseModule,
        DataModule,
        AccountModule,
        SyncModule,
        SduiModule,

        HomeModule,
        FormModule,
        LoginModule,
        DocumentsModule,
        SettingsModule
    )

    viewModelOf(::MainActivityViewModel)
}