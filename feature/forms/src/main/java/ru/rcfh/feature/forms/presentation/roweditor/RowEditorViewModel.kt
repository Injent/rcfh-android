package ru.rcfh.feature.forms.presentation.roweditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.repository.FormRepository
import ru.rcfh.core.sdui.storage.StateCacheStorage
import ru.rcfh.navigation.Screen

data class RowEditorUiState(
    val template: Template.Table? = null,
    val row: Int,
    val rootTemplateId: String,
    val draftId: String,
)

class RowEditorViewModel(
    private val formRepository: FormRepository,
    val cacheStorage: StateCacheStorage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.RowEditor>()

    private val _uiState = MutableStateFlow(
        RowEditorUiState(
            row = route.index,
            rootTemplateId = route.templateId,
            draftId = route.draftId
        )
    )
    val uiState = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            formRepository.getFormTemplates()[0].templates
                .filterIsInstance<Template.Table>()
                .find { it.id == route.templateId }
                .let { temp ->
                    _uiState.update {
                        it.copy(template = temp)
                    }
                }
        }
    }
}