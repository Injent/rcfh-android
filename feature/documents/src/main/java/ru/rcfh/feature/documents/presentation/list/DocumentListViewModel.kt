package ru.rcfh.feature.documents.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pro.respawn.apiresult.onSuccess
import ru.rcfh.core.account.model.RcfhAccount
import ru.rcfh.core.account.repository.AccountRepository
import ru.rcfh.core.sdui.data.DocumentRepository
import ru.rcfh.core.sdui.model.Document
import ru.rcfh.navigation.Navigator

sealed interface DocumentListEvent {
    data class DocumentCreated(val documentId: Int) : DocumentListEvent
}

data class DocumentListUiState(
    val documents: ImmutableList<Document> = persistentListOf(),
    val currentAccount: RcfhAccount? = null,
    val accounts: List<RcfhAccount> = emptyList(),
    val loading: Boolean = true,
)

class DocumentListViewModel(
    private val documentRepository: DocumentRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val _events = Channel<DocumentListEvent>()
    val events = _events.receiveAsFlow()

    val uiState = combine(
        documentRepository.documents,
        accountRepository.accountsFlow,
        accountRepository.currentAccount
    ) { documents, accounts, currentAccount ->
        DocumentListUiState(
            documents = documents.toImmutableList(),
            accounts = accounts,
            currentAccount = currentAccount,
            loading = false
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DocumentListUiState()
        )

    fun onCreateDocument(name: String) {
        viewModelScope.launch {
            documentRepository.create(
                name.fetchDocumentName(uiState.value.documents.map(Document::name))
            )
                .onSuccess { documentId ->
                    _events.send(DocumentListEvent.DocumentCreated(documentId))
                }
        }
    }

    fun onChooseAccount(account: RcfhAccount) {
        viewModelScope.launch {
            accountRepository.choose(account)
        }
    }

    fun back() {
        viewModelScope.launch {
            Navigator.navigateUp()
        }
    }
}

private fun String.fetchDocumentName(existingDocumentNames: List<String>): String {
    val baseName = replace(Regex("\\s*\\(\\d+\\)\\s*$"), "")

    val findDupesRegex = Regex("\\Q$baseName\\E\\s*\\((\\d+)\\)")
    val existingNumbers = existingDocumentNames.mapNotNull {
        findDupesRegex.find(it)?.groupValues?.get(1)?.toInt()
    }

    val newNumber = if (existingNumbers.isNotEmpty()) (existingNumbers.maxOrNull() ?: 0) + 1 else 1
    return if (this in existingDocumentNames) "$baseName ($newNumber)" else this
}