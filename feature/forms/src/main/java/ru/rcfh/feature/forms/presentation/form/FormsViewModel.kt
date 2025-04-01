package ru.rcfh.feature.forms.presentation.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.model.Group
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.repository.FormRepository
import ru.rcfh.core.sdui.storage.StateCacheStorage
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

data class FormsUiState(
    val groupedTemplates: Map<Group, List<Template>> = emptyMap()
)

class FormsViewModel(
    private val formRepository: FormRepository,
    val cacheStorage: StateCacheStorage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val formId = savedStateHandle.toRoute<Screen.Forms>().formId
    val draftId = 0.toString()
    private val _uiState = MutableStateFlow(FormsUiState())
    val uiState = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            cacheStorage.load()
            _uiState.update { data ->
                val formTemplate = formRepository.getFormTemplates()[0]
                val groupedTemplates = formTemplate.groups.associateWith { group ->
                    formTemplate.templates.filter { template -> template.id in group.forms }
                }.toMutableMap()

                val ungroupedTemplates = formTemplate.templates.filter { template ->
                    formTemplate.groups.none { group -> template.id in group.forms }
                }

                if (ungroupedTemplates.isNotEmpty()) {
                    val ungroupedGroup = Group(
                        name = "",
                        forms = ungroupedTemplates.map { it.id }
                    )
                    groupedTemplates[ungroupedGroup] = ungroupedTemplates
                }

                data.copy(
                    groupedTemplates = groupedTemplates.toMap()
                )
            }
        }
    }

    fun onEditSection(templateId: String) {
        Navigator.navigate(Screen.SectionEditor(draftId = draftId, templateId = templateId))
    }

    fun onEditRow(templateId: String, row: Int) {
        viewModelScope.launch {
            Navigator.navigate(
                Screen.RowEditor(
                    templateId = templateId,
                    draftId = draftId,
                    index = row
                )
            )
        }
    }
}