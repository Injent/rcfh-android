package ru.rcfh.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.datastore.SettingsRepository
import ru.rcfh.datastore.model.Prefs
import ru.rcfh.navigation.Navigator

data class SettingsUiState(
    val prefs: Prefs = Prefs(),
    val loading: Boolean = true
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = settingsRepository.data
        .mapLatest {
            SettingsUiState(
                prefs = it.prefs,
                loading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun onUpdatePrefs(prefs: Prefs) {
        viewModelScope.launch {
            settingsRepository.updatePrefs { prefs }
        }
    }

    fun back() {
        viewModelScope.launch {
            Navigator.navigateUp()
        }
    }
}