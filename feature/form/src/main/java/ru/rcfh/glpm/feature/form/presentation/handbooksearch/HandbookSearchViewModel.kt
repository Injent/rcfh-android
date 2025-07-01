package ru.rcfh.glpm.feature.form.presentation.handbooksearch

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.core.sdui.event.SetReference
import ru.rcfh.data.model.Reference
import ru.rcfh.data.repository.HandbookRepository
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

data class HandbookSearchUiState(
    val results: ImmutableList<Reference> = persistentListOf(),
    val title: String,
    val selectedOption: String?
)

class HandbookSearchViewModel(
    private val documentStateManager: DocumentStateManager,
    private val handbookRepository: HandbookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.HandbookSearch>()

    val searchTextState = TextFieldState()
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState = snapshotFlow { searchTextState.text.toString() }
        .debounce(300)
        .mapLatest { query ->
            HandbookSearchUiState(
                title = route.title,
                selectedOption = route.selectedOption,
                results = handbookRepository
                    .search(handbookId = route.handbookId, query = query)
                    .let { results ->
                        if (route.selectedOption.isNullOrEmpty()) {
                            results
                        } else {
                            results.filter { it.name != route.selectedOption }
                        }
                    }
                    .toImmutableList()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HandbookSearchUiState(
                title = route.title,
                selectedOption = route.selectedOption
            )
        )

    fun sendResult(value: String) {
        viewModelScope.launch {
            documentStateManager.loadDocument(documentId = route.documentId).postEvent(
                SetReference(
                    callbackId = route.callbackId,
                    value = value
                )
            )
            Navigator.navigateUp()
        }
    }
}