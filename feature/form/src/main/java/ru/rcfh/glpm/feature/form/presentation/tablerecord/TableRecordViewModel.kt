package ru.rcfh.glpm.feature.form.presentation.tablerecord

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.core.sdui.state.FieldState
import ru.rcfh.core.sdui.state.Table4State
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

class TableRecordUiState(
    val tableName: String = "",
    val fields: ImmutableList<FieldState> = persistentListOf()
)

class TableRecordViewModel(
    private val documentStateManager: DocumentStateManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.TableRecord>()

    val uiState = flow {
        var tableName: String = ""
        documentStateManager.loadDocument(route.documentId)
            .forms[route.formId]!!
            .fields
            .find { it.id == route.templateId }
            .let {
                when (it) {
                    is Table4State -> {
                        tableName = it.name
                        it.rows
                    }
                    else -> emptyList()
                }
            }
            .let { rows ->
                TableRecordUiState(
                    tableName = tableName,
                    fields = rows[route.rowIdx].toImmutableList()
                ).also { emit(it) }
            }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TableRecordUiState()
        )

    fun onBack() {
        viewModelScope.launch {
            Navigator.navigateUp()
        }
    }
}