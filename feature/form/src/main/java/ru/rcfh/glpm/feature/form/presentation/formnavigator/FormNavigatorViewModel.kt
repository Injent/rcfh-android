package ru.rcfh.glpm.feature.form.presentation.formnavigator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.data.DocumentRepository
import ru.rcfh.core.sdui.data.FormRepo
import ru.rcfh.core.sdui.template.FormElement
import ru.rcfh.core.sdui.template.FormOptions
import ru.rcfh.core.sdui.template.FormTab
import ru.rcfh.navigation.Screen

data class FormNavigatorUiState(
    val formElements: ImmutableList<FormElement> = persistentListOf(),
    val formOptions: ImmutableMap<String, Int> = persistentMapOf(),
    val selectedTabIndex: Int,
    val documentId: Int,
    val loading: Boolean = true
)

class FormNavigatorViewModel(
    private val documentRepository: DocumentRepository,
    formRepo: FormRepo,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Screen.FormNavigator>()
    private val _selectedTabIndex = MutableStateFlow(-1)
    val uiState = combine(
        flow { emit(formRepo.getDocumentTemplate().forms) },
        documentRepository.getDocumentOptionsFlow(documentId = route.documentId),
        _selectedTabIndex
    ) { formElements, documentOptions, tabIndex ->
        if (_selectedTabIndex.value == -1) {
            _selectedTabIndex.value = formElements.indexOfFirst { element ->
                when (element) {
                    is FormOptions -> {
                        element.tabs.any { it.formId == route.formId }
                    }
                    is FormTab -> element.formId == route.formId
                }
            }
        }
        FormNavigatorUiState(
            formElements = formElements.toImmutableList(),
            formOptions = documentOptions.formOptions.toImmutableMap(),
            selectedTabIndex = tabIndex.coerceAtLeast(0),
            documentId = route.documentId,
            loading = false
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FormNavigatorUiState(
                selectedTabIndex = _selectedTabIndex.value,
                documentId = route.documentId
            )
        )

    fun setOptionValue(optionId: String, formId: Int) {
        viewModelScope.launch {
            documentRepository.updateOptions(route.documentId) {
                val mutableFormOptions = it.formOptions.toMutableMap()
                mutableFormOptions[optionId] = formId
                it.copy(formOptions = mutableFormOptions)
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTabIndex.value = tabIndex
    }
}