package ru.rcfh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ru.rcfh.core.model.DarkThemeConfig
import ru.rcfh.data.util.SyncManager
import ru.rcfh.datastore.SettingsRepository

data class MainActivityUiState(
    val authed: Boolean = false,
    val loading: Boolean = true,
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.LIGHT
)

class MainActivityViewModel(
    private val syncManager: SyncManager,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    private var syncRequested = false

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = settingsRepository.data
        .mapLatest {
            MainActivityUiState(
                authed = it.currentUserId != null,
                loading = false,
                darkThemeConfig = it.prefs.darkThemeConfig
            )
        }
        .onStart {
            if (!syncRequested) {
                syncManager.requestSync()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainActivityUiState()
        )
}