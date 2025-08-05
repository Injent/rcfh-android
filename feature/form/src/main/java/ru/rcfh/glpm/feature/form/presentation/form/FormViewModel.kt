package ru.rcfh.glpm.feature.form.presentation.form

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.data.DocumentRepository
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.core.sdui.data.FormRepo
import ru.rcfh.core.sdui.state.FormState
import ru.rcfh.core.sdui.state.Table4State
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.core.sdui.template.FormOptions
import ru.rcfh.core.sdui.template.FormTab
import ru.rcfh.glpm.feature.form.R
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

data class FormKoinParams(
    val documentId: Int,
    val formId: Int,
    val optionId: String?
)

sealed interface FormUiState {
    class Success(
        val formState: FormState,
        val formId: Int,
        val formTabs: List<FormTab>
    ) : FormUiState
    data object Loading : FormUiState
}

sealed interface FormEvent {
    data class ShowToast(@StringRes val messageResId: Int) : FormEvent
}

class FormViewModel(
    formRepo: FormRepo,
    private val documentRepository: DocumentRepository,
    private val documentStateManager: DocumentStateManager,
    private val params: FormKoinParams
) : ViewModel() {
    private val _events = Channel<FormEvent>()
    val events = _events.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FormUiState> = documentRepository
        .getDocumentOptionsFlow(params.documentId)
        .mapLatest { options ->
            val state = documentStateManager.loadDocument(documentId = params.documentId)
            val formId = run {
                options.formOptions[params.optionId ?: return@run params.formId] ?: params.formId
            }
            val tabs: List<FormTab> = formRepo.getDocumentTemplate().forms
                .find {
                    it is FormOptions && it.optionId == params.optionId
                }
                ?.let {
                    (it as FormOptions).tabs
                }
                ?: emptyList()
            FormUiState.Success(
                formState = state.forms[formId] ?: error("Form null"),
                formId = formId,
                formTabs = tabs
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FormUiState.Loading
        )

    fun openTable(tableId: String) {
        val state = (uiState.value as FormUiState.Success)
        val isEmpty = state
            .formState
            .fields
            .find { it.id == tableId }
            .let {
                when (it) {
                    is TableState -> it.rows.isEmpty()
                    is Table4State -> it.rows.isEmpty()
                    else -> true
                }
            }

        viewModelScope.launch {
            if (isEmpty) {
                _events.send(FormEvent.ShowToast(R.string.info_tableIsEmpty))
                return@launch
            }

            Navigator.navigate(
                Screen.Table(
                    documentId = params.documentId,
                    formId = state.formId,
                    templateId = tableId
                )
            ) { launchSingleTop = true }
        }
    }

    fun onSetOption(formId: Int) {
        if (params.optionId == null) return
        viewModelScope.launch {
            documentRepository.updateOptions(
                documentId = params.documentId,
                options = {
                    val mutableFormOptions = it.formOptions.toMutableMap()
                    mutableFormOptions[params.optionId] = formId
                    it.copy(formOptions = mutableFormOptions)
                }
            )
        }
    }

    fun openComparisonTable(tableId: String) {
        viewModelScope.launch {
            Navigator.navigate(
                Screen.ComparisonTable(
                    documentId = params.documentId,
                    formId = (uiState.value as FormUiState.Success).formId,
                    templateId = tableId,
                )
            ) { launchSingleTop = true }
        }
    }

    fun back() {
        viewModelScope.launch {
            Navigator.navigateUp()
        }
    }
}