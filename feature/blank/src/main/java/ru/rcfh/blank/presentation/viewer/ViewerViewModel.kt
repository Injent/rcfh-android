package ru.rcfh.blank.presentation.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonElement
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.state.ArrayElement
import ru.rcfh.blank.ui.state.DocumentState
import ru.rcfh.blank.ui.state.TextElement
import ru.rcfh.core.model.FormTab
import ru.rcfh.data.repository.DocumentRepository
import ru.rcfh.data.util.JsonKeys
import kotlin.time.Duration.Companion.seconds

data class ViewerViewModelParams(
    val documentId: Int,
    val initialFormId: Int
)

sealed interface ViewerUiState {

    data class Success(
        val document: DocumentState,
        val formTabs: ImmutableList<FormTab>,
        val selectedTab: FormTab.Tab,
    ) : ViewerUiState

    data object Loading : ViewerUiState

    data object Error : ViewerUiState
}

class ViewerViewModel(
    private val documentRepository: DocumentRepository,
    private val params: ViewerViewModelParams,
) : ViewModel() {
    private val mutex = Mutex()
    private val selectedFormId = MutableStateFlow(params.initialFormId)
    private var documentState: DocumentState? = null
    private var saveJob: Job? = null

    val uiState = combine(
        documentRepository.getDocumentFlow(documentId = params.documentId),
        selectedFormId,
    ) { document, selectedFormId ->
        if (document == null || document.info.formsInUse.isEmpty()) {
            return@combine ViewerUiState.Error
        }

        val formTabs = FormTab.fromIds(document.info.formsInUse).toImmutableList()

        val selectedTab = formTabs.find { it.formId == selectedFormId }
            ?: formTabs.first { it is FormTab.Tab }

        ViewerUiState.Success(
            document = documentState ?: initializeDocument(initialData = document.data),
            formTabs = formTabs,
            selectedTab = selectedTab as? FormTab.Tab ?: error("No active tabs available")
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ViewerUiState.Loading
        )

    private suspend fun initializeDocument(initialData: JsonElement): DocumentState {
        if (documentState != null) return documentState!!

        return mutex.withLock {
            documentState = DocumentState(
                coroutineScope = viewModelScope,
                initialData = initialData
            )
            viewModelScope.launch {
                documentState!!.changeEvent.collect {
                    saveJob?.cancel()
                    saveJob = viewModelScope.launch {
                        delay(1.seconds)
                        documentRepository.saveDocument(
                            documentId = params.documentId,
                            data = documentState!!.state.encodeToJsonElement()
                        )
                    }
                }
            }
            documentState!!
        }
    }

    fun selectTab(id: Int) {
        selectedFormId.value = id
    }

    fun replaceTab(oldId: Int, newId: Int) {
        val uiState = uiState.value as? ViewerUiState.Success ?: return
        val documentScope = documentState!!.state.documentScope

        val formsInUseElement = uiState.document.state
            .queryOrCreate(
                "$.${JsonKeys.FORMS_IN_USE}",
                ArrayElement(documentScope = documentScope)
            )
        val formsInUse = formsInUseElement.mapNotNull { it.intOrNull }

        val newFormInUseIds = FormTab.fromIds(formsInUse).map { tab ->
            if (tab.formId == oldId) {
                newId
            } else {
                tab.formId
            }.let {
                TextElement(
                    content = it.toString(),
                    documentScope = documentScope
                )
            }
        }
        formsInUseElement.clear()
        formsInUseElement.addAll(newFormInUseIds)

        viewModelScope.launch {
            documentRepository.saveDocument(
                documentId = params.documentId,
                data = uiState.document.state.encodeToJsonElement()
            )
        }
    }
}