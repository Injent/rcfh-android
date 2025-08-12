package ru.rcfh.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.rcfh.core.model.FormTab
import ru.rcfh.data.util.JsonKeys
import ru.rcfh.database.entity.DocumentEntity

data class DocumentInfo(
    val id: Int,
    val name: String,
    val ownerId: Int,
    val modificationTimestamp: LocalDateTime,
    val formsInUse: List<Int>,
    val isValid: Boolean,
)

fun DocumentEntity.toDocumentInfo() = DocumentInfo(
    id = id!!,
    name = name,
    ownerId = ownerId,
    modificationTimestamp = modificationTimestamp,
    formsInUse = data.getFormsInUse(),
    isValid = false
)

private fun JsonElement.getFormsInUse(): List<Int> {
    val formsInUse = jsonObject[JsonKeys.FORMS_IN_USE]
        ?.jsonArray
        ?.mapNotNull {
            it.jsonPrimitive.intOrNull
        }

    return if (formsInUse.isNullOrEmpty()) {
        FormTab.preset.filterIsInstance<FormTab.Tab>().map(FormTab.Tab::formId)
    } else formsInUse
}