package ru.rcfh.feature.documents.presentation.details

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.model.FormTab
import ru.rcfh.data.model.DocumentInfo
import ru.rcfh.data.repository.DocumentRepository
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

sealed interface DocumentUiState {
    data class Success(
        val document: DocumentInfo,
        val formTabs: List<FormTab.Tab>
    ) : DocumentUiState
    data object Loading : DocumentUiState
}

class DocumentViewModel(
    private val documentRepository: DocumentRepository,
    private val documentId: Int,
) : ViewModel() {
    private var updateNameJob: Job? = null
    //private val errorReport = MutableStateFlow<ErrorReport?>(null)

    val uiState = documentRepository.getDocumentInfoFlow(documentId = documentId)
        .map { document ->
            if (document == null) return@map DocumentUiState.Loading

            DocumentUiState.Success(
                document = document,
                formTabs = FormTab.fromIds(document.formsInUse).filterIsInstance<FormTab.Tab>()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DocumentUiState.Loading
        )

    fun exportFile(context: Context, onSave: (Uri) -> Unit) {
//        viewModelScope.launch {
//            documentRepository.export(documentId)?.let { content ->
//                FileCreator.createFileInDownloads(
//                    context = context,
//                    fileName = (uiState.value as DocumentUiState.Success).document.name,
//                    content = content
//                )
//            }?.let(onSave)
//        }
    }

    fun detectErrors() {
        viewModelScope.launch {
            //errorReport.value = documentStateManager.detectErrors()
        }
    }

    fun deleteDocument(onComplete: () -> Unit) {
        viewModelScope.launch {
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
                Screen.Viewer(
                    documentId = documentId,
                    formId = formId
                )
            )
        }
    }

    fun updateName(name: String) {
        updateNameJob?.cancel()
        updateNameJob = viewModelScope.launch {
            documentRepository.rename(documentId = documentId, newName = name.trim())
        }
    }
}