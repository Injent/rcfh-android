package ru.rcfh.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.rcfh.database.entity.HandbookEntity
import ru.rcfh.database.entity.ReferenceEntity

@Dao
interface HandbookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertHandbooks(handbook: List<HandbookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertReferences(references: List<ReferenceEntity>)

    @Query("DELETE FROM handbooks")
    suspend fun clear()

    @Transaction
    suspend fun insertHandbooksAndReferences(
        handbooks: List<HandbookEntity>,
        references: List<ReferenceEntity>
    ) {
        insertHandbooks(handbooks)
        insertReferences(references)
    }
}