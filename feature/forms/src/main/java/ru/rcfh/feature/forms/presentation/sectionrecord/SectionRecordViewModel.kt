package ru.rcfh.feature.forms.presentation.sectionrecord

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

sealed interface SectionRecordUiState {
    data class Success(
        val documentId: Int,
        val formId: Int,
        val tableTemplate: Template.ComparisonTable,
        val initialPage: Int,
    ) : SectionRecordUiState
    data object Loading : SectionRecordUiState
    data object Error : SectionRecordUiState
}

class SectionRecordViewModel(
    formRepository: FormRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.SectionRecord>()

    private val _uiState = MutableStateFlow<SectionRecordUiState>(SectionRecordUiState.Loading)
    val uiState = _uiState
        .onStart {
            val form = formRepository.getFormTemplate(route.formId)
            val table = form.templates.find {
                it.id == route.templateId
            } as Template.ComparisonTable

            _uiState.value = SectionRecordUiState.Success(
                documentId = route.documentId,
                formId = form.id,
                initialPage = route.page,
                tableTemplate = table,
            )
        }
        .catch { _uiState.value = SectionRecordUiState.Error }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SectionRecordUiState.Loading
        )
}