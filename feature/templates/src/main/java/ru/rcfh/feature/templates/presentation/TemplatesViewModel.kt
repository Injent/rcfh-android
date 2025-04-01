package ru.rcfh.feature.templates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import ru.rcfh.core.sdui.model.FormTemplate
import ru.rcfh.core.sdui.repository.FormRepository

data class TemplatesUiState(
    val templates: List<FormTemplate> = emptyList()
)

class TemplatesViewModel(
    formRepository: FormRepository,
) : ViewModel() {

    val uiState = flowOf(
        TemplatesUiState(
            templates = runBlocking { formRepository.getFormTemplates() }
        )
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TemplatesUiState()
        )
}