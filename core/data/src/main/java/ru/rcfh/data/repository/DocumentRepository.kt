package ru.rcfh.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.addAll
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray
import pro.respawn.apiresult.ApiResult
import ru.rcfh.core.model.FormTab
import ru.rcfh.common.now
import ru.rcfh.data.model.Document
import ru.rcfh.data.model.DocumentInfo
import ru.rcfh.data.model.toDocument
import ru.rcfh.data.model.toDocumentInfo
import ru.rcfh.data.util.JsonKeys
import ru.rcfh.database.dao.DocumentDao
import ru.rcfh.database.entity.DocumentEntity
import ru.rcfh.datastore.SettingsRepository

interface DocumentRepository {
    val documentInfos: Flow<List<DocumentInfo>>

    fun getDocumentInfoFlow(documentId: Int): Flow<DocumentInfo?>

    suspend fun create(name: String): ApiResult<Int>

    suspend fun rename(documentId: Int, newName: String)

    suspend fun delete(documentId: Int)

    fun getDocumentFlow(documentId: Int): Flow<Document?>

    suspend fun saveDocument(documentId: Int, data: JsonElement)
}

class DocumentRepositoryImpl(
    private val documentDao: DocumentDao,
    private val settingsRepository: SettingsRepository,
) : DocumentRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val documentInfos: Flow<List<DocumentInfo>> = settingsRepository.data
        .mapNotNull { it.currentUserId }
        .flatMapLatest { userId ->
            documentDao.getDocumentsFlow(ownerId = userId)
                .map { entities ->
                    entities.map(DocumentEntity::toDocumentInfo)
                }
        }

    override fun getDocumentInfoFlow(documentId: Int): Flow<DocumentInfo?> {
        return documentDao.getDocumentFlow(documentId)
            .map { it?.toDocumentInfo() }
    }

    override fun getDocumentFlow(documentId: Int): Flow<Document?> {
        return documentDao.getDocumentFlow(documentId)
            .map { it?.toDocument() }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun create(name: String): ApiResult<Int> = ApiResult {
        val userId = settingsRepository.data.map { it.currentUserId }.first().let { userId ->
            requireNotNull(userId) { "User not authorized" }
        }

        val document = DocumentEntity(
            name = name,
            modificationTimestamp = LocalDateTime.now(),
            ownerId = userId,
            data = buildJsonObject {
                put(JsonKeys.SCHEMA_VERSION, JsonPrimitive(1))
                put(JsonKeys.OWNER_ID, JsonPrimitive(userId))
                putJsonArray(JsonKeys.FORMS_IN_USE) {
                    addAll(FormTab.getKnownTabs().map(FormTab.Tab::formId))
                }
            }
        )

        documentDao.insert(document).toInt()
    }

    override suspend fun delete(documentId: Int) {
        documentDao.delete(documentId)
    }

    override suspend fun rename(documentId: Int, newName: String) {
        documentDao.updateName(id = documentId, name = newName)
    }

    override suspend fun saveDocument(documentId: Int, data: JsonElement) {
        documentDao.updateData(documentId = documentId, data = data)
    }
}