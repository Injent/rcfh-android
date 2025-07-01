package ru.rcfh.feature.forms.presentation.sheetrecord

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.repository.FormRepository
import ru.rcfh.navigation.Screen

sealed interface SheetRecordUiState {
    data class Success(
        val documentId: Int,
        val formId: Int,
        val tableTemplate: Template.Table,
        val rowIdx: Int,
    ) : SheetRecordUiState
    data object Loading : SheetRecordUiState
    data object Error : SheetRecordUiState
}

class SheetRecordViewModel(
    formRepository: FormRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.SheetRecord>()
    private val _uiState = MutableStateFlow<SheetRecordUiState>(SheetRecordUiState.Loading)
    val uiState = _uiState
        .onStart {
            val form = formRepository.getFormTemplate(route.formId)
            _uiState.value = runCatching {
                val table = form.templates.find {
                    it.id == route.templateId
                } as Template.Table
                SheetRecordUiState.Success(
                    documentId = route.documentId,
                    formId = form.id,
                    rowIdx = route.rowIdx,
                    tableTemplate = table,
                )
            }.getOrElse { SheetRecordUiState.Error }
        }
        .catch { _uiState.value = SheetRecordUiState.Error }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SheetRecordUiState.Loading
        )
}