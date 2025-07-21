package ru.rcfh.core.sdui.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ExportDocument(
    val docId: Int,
    val name: String,
    val schemaVersion: Int,
    val options: JsonElement,
    val data: List<ExportForm>
)

@Serializable
data class ExportForm(
    val formId: Int,
    val state: JsonElement
)