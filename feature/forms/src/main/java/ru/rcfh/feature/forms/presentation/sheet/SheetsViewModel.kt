package ru.rcfh.feature.forms.presentation.sheet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.repository.FormRepository
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

sealed interface SheetsUiState {
    data class Success(
        val documentId: Int,
        val tableTemplate: Template.Table,
        val formId: Int,
    ) : SheetsUiState
    data object Loading : SheetsUiState
    data object Error : SheetsUiState
}

class SheetsViewModel(
    formRepository: FormRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.Sheet>()
    private val _uiState = MutableStateFlow<SheetsUiState>(SheetsUiState.Loading)
    val uiState = _uiState
        .onStart {
            val form = formRepository.getFormTemplate(route.formId)
            _uiState.value = runCatching {
                SheetsUiState.Success(
                    documentId = route.documentId,
                    formId = form.id,
                    tableTemplate = form.templates.find {
                        it.id == route.templateId
                    } as Template.Table
                )
            }.getOrElse { SheetsUiState.Error }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SheetsUiState.Loading
        )

    fun navigateToSheetRecord(rowIdx: Int) {
        viewModelScope.launch {
            Navigator.navigate(
                Screen.SheetRecord(
                    documentId = route.documentId,
                    formId = route.formId,
                    templateId = route.templateId,
                    rowIdx = rowIdx
                )
            ) { launchSingleTop = true }
        }
    }
}