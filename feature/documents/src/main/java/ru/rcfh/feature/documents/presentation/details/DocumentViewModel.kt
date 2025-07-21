package ru.rcfh.feature.documents.presentation.details

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.common.ErrorReport
import ru.rcfh.core.sdui.data.DocumentRepository
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.core.sdui.data.FileCreator
import ru.rcfh.core.sdui.data.FormRepo
import ru.rcfh.core.sdui.model.Document
import ru.rcfh.core.sdui.template.FormOptions
import ru.rcfh.core.sdui.template.FormTab
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

sealed interface DocumentUiState {
    data class Success(
        val document: Document,
        val forms: List<FormTab>,
        val errorReport: ErrorReport? = null,
    ) : DocumentUiState
    data object Loading : DocumentUiState
}

class DocumentViewModel(
    formRepo: FormRepo,
    private val documentRepository: DocumentRepository,
    private val documentStateManager: DocumentStateManager,
    private val documentId: Int,
) : ViewModel() {
    private var updateNameJob: Job? = null
    private val errorReport = MutableStateFlow<ErrorReport?>(null)

    val uiState = combine(
        documentRepository.getDocumentFlow(documentId),
        flow { emit(formRepo.getDocumentTemplate()) },
        errorReport,
    ) { document, documentTemplate, report ->
        if (document == null) return@combine DocumentUiState.Loading
        val formIds = documentTemplate.forms.map { element ->
            when (element) {
                is FormOptions -> document.options.formOptions[element.optionId]
                is FormTab -> element.formId
            }
        }

        DocumentUiState.Success(
            document = document,
            forms = formRepo.getFormList().filter { it.formId in formIds },
            errorReport = report,
        )
    }
        .onStart {
            viewModelScope.launch {
                documentStateManager.loadDocument(documentId)
                errorReport.value = documentStateManager.detectErrors()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DocumentUiState.Loading
        )

    fun exportFile(context: Context, onSave: (Uri) -> Unit) {
        viewModelScope.launch {
            documentRepository.export(documentId)?.let { content ->
                FileCreator.createFileInDownloads(
                    context = context,
                    fileName = (uiState.value as DocumentUiState.Success).document.name,
                    content = content
                )
            }?.let(onSave)
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

    fun updateName(name: String) {
        updateNameJob?.cancel()
        updateNameJob = viewModelScope.launch {
            documentRepository.updateName(documentId = documentId, name = name.trim())
        }
    }
}