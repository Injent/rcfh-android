package ru.rcfh.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject

@Entity(tableName = "forms", primaryKeys = ["document_id", "form_id"])
data class FormEntity(
    @ColumnInfo("document_id") val documentId: Int,
    @ColumnInfo("form_id") val formId: Int,
    @ColumnInfo("is_valid") val isValid: Boolean,
    @ColumnInfo("state") val state: JsonElement = buildJsonObject {}
)