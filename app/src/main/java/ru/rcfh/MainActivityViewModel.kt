package ru.rcfh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ru.rcfh.core.model.UiTheme
import ru.rcfh.data.util.SyncManager
import ru.rcfh.datastore.SettingsRepository

data class MainActivityUiState(
    val authed: Boolean = false,
    val loading: Boolean = true,
    val uiTheme: UiTheme = UiTheme.LIGHT
)

class MainActivityViewModel(
    private val syncManager: SyncManager,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = settingsRepository.data
        .mapLatest {
            MainActivityUiState(
                authed = it.currentUserId != null,
                loading = false,
                uiTheme = it.prefs.uiTheme
            )
        }
        .onStart {
            syncManager.requestSync()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainActivityUiState()
        )
}