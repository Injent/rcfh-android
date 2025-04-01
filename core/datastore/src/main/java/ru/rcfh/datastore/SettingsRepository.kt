package ru.rcfh.datastore

import androidx.datastore.core.DataStore
import ru.rcfh.datastore.model.DataVersion
import ru.rcfh.datastore.model.UserSettings

class SettingsRepository(
    private val dataStore: DataStore<UserSettings>
) {
    val data = dataStore.data

    suspend fun updateDataVersions(block: (DataVersion) -> DataVersion) {
        dataStore.updateData {
            it.copy(dataVersion = block(it.dataVersion))
        }
    }
}