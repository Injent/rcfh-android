package ru.rcfh.feature.forms.presentation.handbooksearch

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import ru.rcfh.core.sdui.storage.StateCacheStorage
import ru.rcfh.data.model.Reference
import ru.rcfh.data.repository.HandbookRepository
import ru.rcfh.navigation.Screen

data class HandbookDialogUiState(
    val results: List<Reference> = emptyList(),
)

class HandbookDialogViewModel(
    private val handbookRepository: HandbookRepository,
    val cacheStorage: StateCacheStorage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val route = savedStateHandle.toRoute<Screen.HandbookDialog>()

    val searchTextState = TextFieldState()
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState = snapshotFlow { searchTextState.text.toString() }
        .debounce(300)
        .mapLatest { query ->
            if (query.isBlank()) return@mapLatest HandbookDialogUiState()

            HandbookDialogUiState(
                results = handbookRepository.search(handbookId = route.handbookId, query = query)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HandbookDialogUiState()
        )
}