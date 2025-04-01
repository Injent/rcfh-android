package ru.rcfh.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "cache_state",
    primaryKeys = ["draft_id", "path"]
)
data class StateCacheEntity(
    @ColumnInfo("draft_id") val draftId: String,
    @ColumnInfo("path") val path: String,
    @ColumnInfo("value") val value: List<List<String>>
)
