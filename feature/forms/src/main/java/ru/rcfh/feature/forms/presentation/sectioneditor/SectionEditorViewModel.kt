package ru.rcfh.feature.forms.presentation.sectioneditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.repository.FormRepository
import ru.rcfh.core.sdui.storage.StateCacheStorage
import ru.rcfh.navigation.Screen

sealed interface SectionEditorUiState {
    data class Success(
        val template: Template.ComparisonTable
    ) : SectionEditorUiState
    data object Loading : SectionEditorUiState
    data object Error : SectionEditorUiState
}

class SectionEditorViewModel(
    private val formRepository: FormRepository,
    val cacheStorage: StateCacheStorage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val route = savedStateHandle.toRoute<Screen.SectionEditor>()

    val uiState = flow {
        formRepository.getFormTemplates()[0].templates
            .filterIsInstance<Template.ComparisonTable>()
            .find { it.id == route.templateId }
            ?.let { temp ->
                SectionEditorUiState.Success(
                    template = temp
                ).also { emit(it) }
            } ?: run {
                emit(SectionEditorUiState.Error)
            }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SectionEditorUiState.Loading
        )
}