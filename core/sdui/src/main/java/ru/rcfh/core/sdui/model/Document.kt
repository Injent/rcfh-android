package ru.rcfh.core.sdui.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import ru.rcfh.core.sdui.template.DocumentOptions
import ru.rcfh.database.entity.DocumentEntity

data class Document(
    val id: Int,
    val name: String,
    val modificationTimestamp: LocalDateTime,
    val ownerId: Int,
    val options: DocumentOptions,
    val isValid: Boolean,
)

fun Document.toEntity(format: Json) = DocumentEntity(
    id = id,
    name = name,
    modificationTimestamp = modificationTimestamp,
    options = format.encodeToJsonElement(options),
    ownerId = ownerId
)

fun DocumentEntity.toExternalModel(options: DocumentOptions, isValid: Boolean) = Document(
    id = id!!,
    name = name,
    modificationTimestamp = modificationTimestamp,
    options = options,
    isValid = isValid,
    ownerId = ownerId
)