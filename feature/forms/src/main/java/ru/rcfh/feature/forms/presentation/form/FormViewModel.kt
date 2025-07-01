package ru.rcfh.feature.forms.presentation.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.model.FormTemplate
import ru.rcfh.core.sdui.model.Group
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.repository.FormRepository
import ru.rcfh.core.sdui.repository.ValidationRepository
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

data class FormTab(
    val formId: Int,
    val name: String
)

sealed interface FormUiState {
    data class FormsUiState(
        val groupedTemplates: Map<Group, List<Template>> = emptyMap(),
        val formsTabs: List<FormTab>,
        val form: FormTemplate,
        val documentId: Int,
        val invalidFormIds: List<Int> = emptyList()
    ) : FormUiState
    data object Loading : FormUiState
}

class FormViewModel(
    private val formRepository: FormRepository,
    private val validationRepository: ValidationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.Forms>()

    private val _uiState = MutableStateFlow<FormUiState>(FormUiState.Loading)
    val uiState = _uiState
        .onStart {
            load()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FormUiState.Loading,
        )

    fun back() {
        viewModelScope.launch {
            Navigator.navigateUp()
        }
    }

    fun navigateToSheets(templateId: String) {
        viewModelScope.launch {
            Navigator.navigate(
                Screen.Sheet(
                    documentId = route.documentId,
                    formId = route.formId,
                    templateId = templateId
                )
            ) { launchSingleTop = true }
        }
    }

    fun navigateToSections(templateId: String, page: Int) {
        viewModelScope.launch {
            Navigator.navigate(
                Screen.SectionRecord(
                    documentId = route.documentId,
                    formId = route.formId,
                    templateId = templateId,
                    page = page
                )
            ) { launchSingleTop = true }
        }
    }

    private fun load() {
        viewModelScope.launch {
            val formTemplate = formRepository.getFormTemplate(route.formId)
            val groupedTemplates = formTemplate.groups.associateWith { group ->
                formTemplate.templates.filter { template -> template.id in group.forms }
            }.toMutableMap()

            val ungroupedTemplates = formTemplate.templates.filter { template ->
                formTemplate.groups.none { group -> template.id in group.forms }
            }

            if (ungroupedTemplates.isNotEmpty()) {
                val ungroupedGroup = Group(
                    name = null,
                    forms = ungroupedTemplates.map { it.id }
                )
                groupedTemplates[ungroupedGroup] = ungroupedTemplates
            }

            _uiState.value = FormUiState.FormsUiState(
                groupedTemplates = groupedTemplates.toMap(),
                form = formTemplate,
                formsTabs = formRepository.getFormTemplates().map {
                    FormTab(formId = it.id, name = it.name.substringBefore('.'))
                },
                documentId = route.documentId,
                invalidFormIds = validationRepository.getInvalidFormIds(route.documentId)
            )
        }
    }
}