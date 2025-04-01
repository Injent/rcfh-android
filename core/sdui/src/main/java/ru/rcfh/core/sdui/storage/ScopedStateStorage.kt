package ru.rcfh.core.sdui.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScopedStateStorage internal constructor(
    private val stateCacheStorage: StateCacheStorage,
    val draftId: String,
) {
    suspend fun putText(
        templateId: String,
        value: String,
        parentTemplateId: String? = null,
        row: Int = 0,
        iteration: Int = 0,
        instantWrite: Boolean = false
    ) {
        stateCacheStorage.putState(
            draftId = draftId,
            path = parentTemplateId join templateId,
            value = value,
            index = row,
            innerIndex = iteration,
            instantWrite = instantWrite
        )
    }

    fun getText(
        templateId: String,
        parentTemplateId: String? = null,
        row: Int = 0,
        iteration: Int = 0
    ): String {
        return stateCacheStorage.getState(
            draftId = draftId,
            path = parentTemplateId join templateId,
            index = row,
            innerIndex = iteration
        ) ?: ""
    }

    suspend fun removeRepeatable(
        parentTemplateId: String,
        row: Int = 0,
        iteration: Int
    ) {
        stateCacheStorage.removeInnerIndexedState(
            draftId = draftId,
            rootPath = parentTemplateId,
            index = row,
            innerIndex = iteration
        )
    }

    suspend fun removeRow(
        parentTemplateId: String,
        row: Int
    ) {
        stateCacheStorage.removeIndexedState(
            draftId = draftId,
            path = parentTemplateId,
            index = row
        )
    }

    fun observeText(
        parentTemplateId: String,
        templateId: String,
        row: Int,
        iteration: Int = 0
    ): Flow<String> {
        return stateCacheStorage.observeState(
            draftId = draftId,
            path = parentTemplateId join templateId,
        )
            .map { state ->
                if (state == null) return@map ""

                state.value.getOrNull(row)?.getOrNull(iteration) ?: ""
            }
    }

    fun observeTableRowCount(
        parentTemplateId: String
    ): Flow<Int> {
        return stateCacheStorage.observeTable(
            draftId = draftId,
            path = parentTemplateId
        )
            .map { states ->
                states.maxOfOrNull { it.value.size } ?: 0
            }
    }

    fun observeIterationCount(
        templateId: String,
        row: Int = 0
    ): Flow<Int> {
        return stateCacheStorage.observeTable(
            draftId = draftId,
            path = templateId,
        )
            .map { states ->
                states.maxOfOrNull {
                    it.value.getOrNull(row)?.size ?: 0
                } ?: 0
            }
    }
}

private infix fun String?.join(value: String): String {
    return (this?.let { "$it/" } ?: "") + value
}