package ru.rcfh.glpm.feature.form.presentation.comparisontable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.core.sdui.state.ComparisonTableState
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

sealed interface ComparisonTableUiState {
    class Success(
        val state: ComparisonTableState,
    ) : ComparisonTableUiState
    data object Loading : ComparisonTableUiState
}

class ComparisonTableViewModel(
    private val documentStateManager: DocumentStateManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.ComparisonTable>()

    val uiState = flow {
        val document = documentStateManager.loadDocument(documentId = route.documentId)
        document.forms[route.formId]!!
            .fields
            .find { it is ComparisonTableState && it.id == route.templateId }
            .let { it as ComparisonTableState }
            .let { state ->
                ComparisonTableUiState.Success(
                    state = state
                ).also { emit(it) }
            }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ComparisonTableUiState.Loading
        )

    fun onBack() {
        viewModelScope.launch {
            Navigator.navigateUp()
        }
    }
}