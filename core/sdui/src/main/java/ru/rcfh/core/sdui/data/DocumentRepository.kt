package ru.rcfh.core.sdui.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import pro.respawn.apiresult.ApiResult
import pro.respawn.apiresult.onError
import ru.rcfh.common.now
import ru.rcfh.core.account.repository.AccountRepository
import ru.rcfh.core.sdui.model.Document
import ru.rcfh.core.sdui.model.toExternalModel
import ru.rcfh.core.sdui.template.DocumentOptions
import ru.rcfh.core.sdui.template.FormOptions
import ru.rcfh.core.sdui.template.FormTab
import ru.rcfh.database.dao.DocumentDao
import ru.rcfh.database.dao.FormDao
import ru.rcfh.database.entity.DocumentEntity
import timber.log.Timber

class DocumentRepository(
    private val formRepo: FormRepo,
    private val documentDao: DocumentDao,
    private val formDao: FormDao,
    private val accountRepository: AccountRepository,
) {
    private val format = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalCoroutinesApi::class)
    val documents: Flow<List<Document>> = accountRepository
        .currentAccount
        .flatMapLatest { account ->
            if (account == null) {
                flowOf(emptyList())
            } else {
                documentDao.getDocumentsFlow(ownerId = account.userId).map { entities ->
                    entities.map { entity ->
                        val options = runCatching {
                            format.decodeFromJsonElement<DocumentOptions>(entity.options)
                        }.getOrDefault(DocumentOptions())
                        val formIds = formRepo.getDocumentTemplate().forms.mapNotNull { element ->
                            when (element) {
                                is FormOptions -> options.formOptions[element.optionId]
                                is FormTab -> element.formId
                            }
                        }
                        val formsValidity = formDao.getFormsValidity(entity.id!!)
                        val isValid = formsValidity
                            .filter { it.id in formIds }
                            .all { it.isValid }
                        entity.toExternalModel(options, isValid && formsValidity.isNotEmpty())
                    }
                }
            }
        }

    fun getDocumentOptionsFlow(documentId: Int): Flow<DocumentOptions> {
        return documentDao.getOptionsFlow(documentId).map {
            runCatching {
                format.decodeFromJsonElement<DocumentOptions>(it)
            }.getOrDefault(DocumentOptions())
        }
    }

    suspend fun getDocumentOptions(documentId: Int): DocumentOptions {
        return runCatching {
            format.decodeFromJsonElement<DocumentOptions>(documentDao.getOptions(documentId))
        }.getOrDefault(DocumentOptions())
    }

    fun getDocumentFlow(documentId: Int): Flow<Document?> {
        return documentDao.getDocumentFlow(documentId).map { entity ->
            if (entity == null) return@map null
            val options = runCatching { format.decodeFromJsonElement<DocumentOptions>(entity.options) }
                .getOrDefault(DocumentOptions())
            val formIds = formRepo.getDocumentTemplate().forms.mapNotNull { element ->
                when (element) {
                    is FormOptions -> options.formOptions[element.optionId]
                    is FormTab -> element.formId
                }
            }
            val isValid = formDao.getFormsValidity(documentId)
                .filter { it.id in formIds }
                .all { it.isValid }
            entity.toExternalModel(options, isValid)
        }
    }

    suspend fun create(name: String): ApiResult<Int> = ApiResult {
        DocumentEntity(
            name = name,
            modificationTimestamp = LocalDateTime.now(),
            ownerId = getUserIdOrThrow(),
        ).let {
            documentDao.insert(it).toInt()
        }
    }
        .onError { e ->
            Timber.e(e)
        }

    suspend fun delete(documentId: Int) {
        println(documentId)
        formDao.deleteByDocumentId(documentId)
        documentDao.delete(documentId)
    }

    suspend fun updateName(documentId: Int, name: String) {
        documentDao.updateName(documentId, name)
    }

    suspend fun updateOptions(
        documentId: Int,
        options: (DocumentOptions) -> DocumentOptions
    ): ApiResult<Unit> = ApiResult {
        runCatching {
            format.decodeFromJsonElement<DocumentOptions>(documentDao.getOptions(documentId))
        }
            .getOrDefault(DocumentOptions())
            .let {
                documentDao.updateOptions(
                    documentId = documentId,
                    options = format.encodeToJsonElement(options(it))
                )
            }
    }
        .onError { e ->
            Timber.e(e)
        }

    @Throws(IllegalStateException::class)
    private suspend fun getUserIdOrThrow(): Int {
        return accountRepository.currentAccount.first()
            ?.userId
            ?: error("User not signed in")
    }
}