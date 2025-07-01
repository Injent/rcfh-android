package ru.rcfh.feature.documents.presentation.details

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.common.ErrorReport
import ru.rcfh.core.sdui.data.DocumentRepository
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.core.sdui.data.FormRepo
import ru.rcfh.core.sdui.model.Document
import ru.rcfh.core.sdui.template.FormOptions
import ru.rcfh.core.sdui.template.FormTab
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

data class DocumentUiState(
    val document: Document? = null,
    val forms: List<FormTab> = emptyList(),
    val errorReport: ErrorReport? = null,
)

class DocumentViewModel(
    formRepo: FormRepo,
    private val documentRepository: DocumentRepository,
    private val documentStateManager: DocumentStateManager,
    private val documentId: Int,
) : ViewModel() {
    val nameFieldState = TextFieldState(initialText = "...")
    private val nameFlow = snapshotFlow { nameFieldState.text.toString() }
    private val errorReport = MutableStateFlow<ErrorReport?>(null)

    val uiState = combine(
        nameFlow,
        documentRepository.getDocumentFlow(documentId),
        flow { emit(formRepo.getDocumentTemplate()) },
        errorReport,
    ) { name, document, documentTemplate, report ->
        if (document == null) return@combine DocumentUiState()
        val formIds = documentTemplate.forms.map { element ->
            when (element) {
                is FormOptions -> document.options.formOptions[element.optionId]
                is FormTab -> element.formId
            }
        }

        DocumentUiState(
            document = document.copy(name = name),
            forms = formRepo.getFormList().filter { it.formId in formIds },
            errorReport = report,
        )
    }
        .onStart {
            viewModelScope.launch {
                documentStateManager.loadDocument(documentId)
                detectErrors()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DocumentUiState()
        )

    init {
        viewModelScope.run {
            launch {
                // Updating document name from TextFieldState with delay
                startDelayedNameUpdate()
            }
            launch {
                // Set initial name for TextFieldState from database
                documentRepository.getDocumentFlow(documentId).firstOrNull()?.let {
                    nameFieldState.setTextAndPlaceCursorAtEnd(it.name)
                }
            }
        }
    }

    fun detectErrors() {
        viewModelScope.launch {
            errorReport.value = documentStateManager.detectErrors()
        }
    }

    fun deleteDocument(onComplete: () -> Unit) {
        viewModelScope.launch {
            documentStateManager.closeUnsaved()
            documentRepository.delete(documentId)
            onComplete()
        }
    }

    fun navigateToSummarize() {
        viewModelScope.launch {
            Navigator.navigate(
                Screen.Summarize(documentId = documentId)
            ) { launchSingleTop = true }
        }
    }

    fun navigateToForm(formId: Int) {
        viewModelScope.launch {
            Navigator.navigate(
                Screen.FormNavigator(
                    documentId = documentId,
                    formId = formId
                )
            )
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun startDelayedNameUpdate() {
        nameFlow
            .debounce(500)
            .collectLatest {
                if (it.isBlank()) return@collectLatest
                documentRepository.updateName(documentId = documentId, name = it)
            }
    }
}