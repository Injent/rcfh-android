package ru.rcfh.core.sdui.data

import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import ru.rcfh.core.sdui.state.FormState
import ru.rcfh.database.dao.FormDao
import ru.rcfh.database.entity.FormEntity

class FormStateManager(
    private val formRepo: FormRepo,
    private val formDao: FormDao,
) {
    private val format = Json { ignoreUnknownKeys = true }

    suspend fun save(documentId: Int, formState: FormState) = withContext(Dispatchers.Default) {
        val jsonData = buildJsonObject {
            formState.fields.forEach { field ->
                put(field.id, field.save())
            }
        }

        formDao.upsert(
            FormEntity(
                documentId = documentId,
                formId = formState.id,
                isValid = formState.isValid(),
                state = jsonData
            )
        )
    }

    suspend fun get(documentId: Int, formId: Int): FormState? {
        val form = formRepo.getForm(formId)!!
        return FormState(
            id = formId,
            name = form.name,
            fields = persistentListOf()
        )
    }
}