package ru.rcfh.di

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.GlpmApplication
import ru.rcfh.MainActivityViewModel
import ru.rcfh.blank.di.BlankModule
import ru.rcfh.common.AppFiles
import ru.rcfh.common.di.CommonModule
import ru.rcfh.core.account.di.AccountModule
import ru.rcfh.core.sync.di.SyncModule
import ru.rcfh.data.di.DataModule
import ru.rcfh.database.di.DatabaseModule
import ru.rcfh.datastore.di.DataStoreModule
import ru.rcfh.feature.documents.di.DocumentsModule
import ru.rcfh.feature.login.di.LoginModule
import ru.rcfh.feature.settings.di.SettingsModule
import ru.rcfh.network.di.NetworkModule

val AppModule = module {
    single {
        (androidApplication() as GlpmApplication).appScope
    }
    single {
        val context = androidContext()
        AppFiles(
            dataDir = context.dataDir,
            cacheDir = context.cacheDir,
            filesDir = context.filesDir
        )
    }

    includes(
        CommonModule,
        NetworkModule,
        DataStoreModule,
        DatabaseModule,
        DataModule,
        AccountModule,
        SyncModule,

        BlankModule,
        LoginModule,
        DocumentsModule,
        SettingsModule
    )

    viewModelOf(::MainActivityViewModel)
}