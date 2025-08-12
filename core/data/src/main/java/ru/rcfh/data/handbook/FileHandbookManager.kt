package ru.rcfh.data.handbook

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import pro.respawn.apiresult.orElse
import ru.rcfh.common.AppDispatchers
import ru.rcfh.common.AppFiles
import ru.rcfh.data.model.ReferenceObj
import ru.rcfh.datastore.SettingsRepository
import ru.rcfh.network.ktor.KtorService
import java.io.File

interface HandbookManager {

    suspend fun save(handbookId: Int, references: List<JsonObject>)

    @Throws(SerializationException::class, IllegalArgumentException::class)
    suspend fun load(handbookId: Int): List<ReferenceObj>

    suspend fun sync(): Boolean
}

class FileHandbookManager(
    private val settingsRepository: SettingsRepository,
    private val ktorService: KtorService,
    private val dispatchers: AppDispatchers,
    appFiles: AppFiles
) : HandbookManager {
    private val json = Json { ignoreUnknownKeys = true }
    private val storageDir = File(appFiles.filesDir, "handbooks").apply { mkdirs() }

    override suspend fun save(handbookId: Int, references: List<JsonObject>) {
        withContext(dispatchers.ioDispatcher) {
            val file = getFileForHandbook(handbookId)
            file.writeText(json.encodeToString(references))
        }
    }

    @Throws(SerializationException::class, IllegalArgumentException::class)
    override suspend fun load(handbookId: Int): List<ReferenceObj> = withContext(dispatchers.ioDispatcher) {
        val file = getFileForHandbook(handbookId = handbookId)
        return@withContext if (file.exists()) {
            val jsonString = file.readText()
            json.decodeFromString<List<JsonObject>>(jsonString).map(::ReferenceObj)
        } else {
            emptyList()
        }
    }

    override suspend fun sync(): Boolean = withContext(dispatchers.ioDispatcher) {
        val localVersion = settingsRepository.data.first().dataVersion.handbooks

        val handbooks = ktorService.getHandbookCollection()
            .orElse { return@withContext false }

        if (localVersion == handbooks.version) {
            return@withContext true
        }
        handbooks.handbooks.forEach { handbook ->
            save(
                handbookId = handbook.id,
                references = handbook.references
            )
        }
        settingsRepository.updateDataVersions { it.copy(handbooks = handbooks.version) }
        return@withContext true
    }

    private fun getFileForHandbook(handbookId: Int): File {
        return File(storageDir, "${handbookId}.json")
    }
}