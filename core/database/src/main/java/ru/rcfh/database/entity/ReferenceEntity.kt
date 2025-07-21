package ru.rcfh.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    "refs",
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
    @ColumnInfo("description")
    val description: String? = null,
    @ColumnInfo("code")
    val code: Int? = null,
    @ColumnInfo("sign_codes")
    val signCodes: List<Int>? = null,
    @ColumnInfo("handbook_id")
    val handbookId: Int,
)