package ru.rcfh.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.rcfh.datastore.SettingsRepository
import ru.rcfh.datastore.model.UserSettings
import ru.rcfh.datastore.serializer.SettingsSerializer

private const val DATASTORE_FILE = "settings.json"

val DataStoreModule = module {
    single<DataStore<UserSettings>> {
        DataStoreFactory.create(
            serializer = SettingsSerializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            corruptionHandler = ReplaceFileCorruptionHandler {
                UserSettings()
            },
        ) {
            androidContext().dataStoreFile(DATASTORE_FILE)
        }
    }
    singleOf(::SettingsRepository)
}