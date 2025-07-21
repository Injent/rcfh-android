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
import ru.rcfh.core.sdui.common.RefDependency
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.core.sdui.event.SetReference
import ru.rcfh.data.model.Reference
import ru.rcfh.data.repository.HandbookRepository
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

data class HandbookSearchUiState(
    val results: ImmutableList<Reference> = persistentListOf(),
    val title: String,
    val selectedOption: String?,
    val dependencyIsNotFilled: Boolean = false
)

class HandbookSearchViewModel(
    private val documentStateManager: DocumentStateManager,
    private val handbookRepository: HandbookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.HandbookSearch>()
    private val dependencyIsNotFilled = route.shouldHaveFilledDependency && route.dependencyRefId == null

    val searchTextState = TextFieldState()
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState = snapshotFlow { searchTextState.text.toString() }
        .debounce(300)
        .mapLatest { query ->
            HandbookSearchUiState(
                title = route.title,
                selectedOption = route.selectedOption,
                dependencyIsNotFilled = dependencyIsNotFilled,
                results = handbookRepository
                    .search(
                        handbookId = route.handbookId,
                        query = query,
                        dependencyHandbook = route.dependencyHandbook,
                        dependencyRefId = route.dependencyRefId
                    )
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
                selectedOption = route.selectedOption,
                dependencyIsNotFilled = dependencyIsNotFilled
            )
        )

    fun onSelectReference(reference: Reference) {
        viewModelScope.launch {
            documentStateManager.loadDocument(documentId = route.documentId).postEvent(
                SetReference(
                    callbackId = route.callbackId,
                    value = reference.name,
                    templateId = route.templateId,
                    rowIndex = route.rowIndex,
                    refDependency = RefDependency(
                        handbookId = route.handbookId,
                        refId = reference.id
                    )
                )
            )
            Navigator.navigateUp()
        }
    }
}