package ru.rcfh.blank.presentation.referencesearch

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.rcfh.blank.ui.search.FilterLogicPreset
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.data.handbook.HandbookManager
import ru.rcfh.data.model.ReferenceObj

data class ReferenceSearchUiState(
    val loading: Boolean = true,
    val searchResults: List<ReferenceObj> = emptyList(),
)

class ReferenceSearchViewModel(
    private val handbookManager: HandbookManager,
    private val route: ReferenceSearchScreen,
    private val state: Element,
) : ViewModel() {
    val searchTextState = TextFieldState()

    val uiState = combine(
        snapshotFlow { searchTextState.text.toString() },
        flow { emit(handbookManager.load(route.handbookId)) },
    ) { query, items ->
        val filterLogic = FilterLogicPreset[route.resultPath]
        val filteredItems = if (filterLogic != null) {
            items.filter { filterLogic.isMatching(state, it) }
        } else {
            items
        }

        ReferenceSearchUiState(
            loading = false,
            searchResults = route.searchLogic.search(query, filteredItems),
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReferenceSearchUiState()
        )
}