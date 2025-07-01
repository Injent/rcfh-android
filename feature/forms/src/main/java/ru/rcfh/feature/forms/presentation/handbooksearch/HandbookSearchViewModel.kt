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
import ru.rcfh.data.model.Reference
import ru.rcfh.data.repository.HandbookRepository
import ru.rcfh.navigation.Screen

data class HandbookSearchUiState(
    val results: List<Reference> = emptyList(),
)

class HandbookSearchViewModel(
    private val handbookRepository: HandbookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.HandbookSearch>()

    val searchTextState = TextFieldState()
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState = snapshotFlow { searchTextState.text.toString() }
        .debounce(300)
        .mapLatest { query ->
            if (query.isBlank()) return@mapLatest HandbookSearchUiState()
            HandbookSearchUiState(
                results = handbookRepository.search(handbookId = route.handbookId, query = query)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HandbookSearchUiState()
        )
}