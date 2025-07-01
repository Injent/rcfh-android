package ru.rcfh.datastore

import androidx.datastore.core.DataStore
import ru.rcfh.datastore.model.DataVersion
import ru.rcfh.datastore.model.Prefs
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

    suspend fun updatePrefs(block: (Prefs) -> Prefs) {
        dataStore.updateData {
            it.copy(prefs = block(it.prefs))
        }
    }

    suspend fun setCurrentUserId(userId: Int) {
        dataStore.updateData { it.copy(currentUserId = userId) }
    }
}