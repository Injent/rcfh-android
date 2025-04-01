package ru.rcfh.database.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.rcfh.database.entity.HandbookEntity
import ru.rcfh.database.entity.ReferenceEntity

data class HandbookAndReferences(
    @Embedded
    val handbook: HandbookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "handbook_id"
    )
    val references: List<ReferenceEntity>
)