package ru.rcfh.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "references",
    foreignKeys = [
        ForeignKey(
            entity = HandbookEntity::class,
            parentColumns = ["id"],
            childColumns = ["handbook_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["id", "handbook_id"],
    indices = [Index(value = ["handbook_id"])]
)
data class ReferenceEntity(
    @ColumnInfo("id")
    val id: Int,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("handbook_id")
    val handbookId: Int
)
