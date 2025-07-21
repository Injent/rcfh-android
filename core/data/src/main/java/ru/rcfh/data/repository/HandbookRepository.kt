package ru.rcfh.data.repository

import kotlinx.coroutines.flow.first
import pro.respawn.apiresult.orElse
import ru.rcfh.data.model.Reference
import ru.rcfh.data.model.mapper.toEntity
import ru.rcfh.data.model.mapper.toExternalModel
import ru.rcfh.database.dao.HandbookDao
import ru.rcfh.database.dao.ReferenceDao
import ru.rcfh.database.entity.ReferenceEntity
import ru.rcfh.datastore.SettingsRepository
import ru.rcfh.network.ktor.KtorService
import ru.rcfh.network.model.NetworkHandbookCollection

class HandbookRepository(
    private val service: KtorService,
    private val handbookDao: HandbookDao,
    private val referenceDao: ReferenceDao,
    private val settingsRepository: SettingsRepository
) {
    suspend fun sync(): Boolean {
        val localVersion = settingsRepository.data.first().dataVersion.handbooks

        val handbookCollection = service.getHandbookCollection()
            .orElse { return false }

        // TODO расскомментировать когда Миша добавит version
        //if (localVersion == handbookCollection.version) return true

        handbookDao.clear()
        referenceDao.clear()

        val handbookEntities = handbookCollection.handbooks
            .map(NetworkHandbookCollection.Handbook::toEntity)
        val referenceEntities = handbookCollection.handbooks
            .map { handbook ->
                handbook.references.map { it.toEntity(handbookId = handbook.id) }
            }
            .flatten()

        handbookDao.insertHandbooksAndReferences(
            handbooks = handbookEntities,
            references = referenceEntities
        )
        settingsRepository.updateDataVersions { it.copy(handbooks = handbookCollection.version) }
        return true
    }

    suspend fun search(
        handbookId: Int,
        query: String,
        dependencyHandbook: Int? = null,
        dependencyRefId: Int? = null
    ): List<Reference> {
        val codes = if (dependencyHandbook != null && dependencyRefId != null) {
            referenceDao.getReference(
                handbookId = dependencyHandbook,
                id = dependencyRefId
            )?.signCodes
        } else null

        return if (query.isBlank()) {
            if (codes == null) {
                referenceDao.getRefs(handbookId = handbookId)
            } else {
                referenceDao.getRefs(handbookId = handbookId, codes = codes)
            }
        } else {
            referenceDao.search(
                handbookId = handbookId,
                query = query.toFtsQuery(),
                codes = codes
            )
                .sortedByDescending { it.rank }
                .map { it.reference }
        }
            .map(ReferenceEntity::toExternalModel)
    }
}

fun String.toFtsQuery(): String {
    val replacedQuery = replace("\"", "\"\"")
    return "\"$replacedQuery*\""
}