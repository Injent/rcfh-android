package ru.rcfh.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") var id: Int? = null,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("modification_timestamp") val modificationTimestamp: LocalDateTime,
    @ColumnInfo("owner_id") val ownerId: Int,
    @ColumnInfo("options") val options: JsonElement = buildJsonObject {}
)