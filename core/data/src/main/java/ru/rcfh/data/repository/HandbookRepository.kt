package ru.rcfh.data.repository

import kotlinx.coroutines.flow.first
import pro.respawn.apiresult.onError
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

        if (localVersion == handbookCollection.version) return true

        handbookDao.clear()
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

    suspend fun search(handbookId: Int, query: String): List<Reference> {
        return referenceDao.search(handbookId = handbookId, query = query)
            .map(ReferenceEntity::toExternalModel)
    }
}