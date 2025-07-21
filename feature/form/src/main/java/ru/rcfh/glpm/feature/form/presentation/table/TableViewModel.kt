package ru.rcfh.glpm.feature.form.presentation.table

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

class TableViewModel(
    documentStateManager: DocumentStateManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.Table>()

    val tableState = flow {
        documentStateManager.loadDocument(route.documentId)
            .forms[route.formId]!!
            .fields
            .find { it.id == route.templateId && it is TableState }
            .let { it as TableState }
            .let { emit(it) }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onBack() {
        viewModelScope.launch {
            Navigator.navigateUp()
        }
    }

    fun onEditRow(rowIdx: Int) {
        viewModelScope.launch {
            Navigator.navigate(
                Screen.TableRecord(
                    documentId = route.documentId,
                    formId = route.formId,
                    templateId = tableState.value?.id ?: return@launch,
                    rowIdx = rowIdx
                )
            )
        }
    }
}