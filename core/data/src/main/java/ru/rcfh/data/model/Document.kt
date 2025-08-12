package ru.rcfh.data.model

import kotlinx.serialization.json.JsonElement
import ru.rcfh.database.entity.DocumentEntity

data class Document(
    val info: DocumentInfo,
    val data: JsonElement
)

fun DocumentEntity.toDocument() = Document(
    info = toDocumentInfo(),
    data = data
)