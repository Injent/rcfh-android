package ru.rcfh.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonElement
import ru.rcfh.database.entity.DocumentEntity

@Dao
interface DocumentDao {

    @Query("SELECT options FROM documents WHERE id = :documentId LIMIT 1")
    suspend fun getOptions(documentId: Int): JsonElement

    @Query("SELECT options FROM documents WHERE id = :documentId LIMIT 1")
    fun getOptionsFlow(documentId: Int): Flow<JsonElement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: DocumentEntity): Long

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    suspend fun get(id: Int): DocumentEntity?

    @Query("UPDATE documents SET name = :name WHERE id = :id")
    suspend fun updateName(id: Int, name: String)

    @Query("""
        SELECT *
        FROM documents d
        WHERE d.owner_id = :ownerId
    """)
    fun getDocumentsFlow(ownerId: Int): Flow<List<DocumentEntity>>

    @Query("""
        SELECT * FROM documents d
        WHERE d.id = :documentId
        LIMIT 1
    """)
    fun getDocumentFlow(documentId: Int): Flow<DocumentEntity?>

    @Query("DELETE FROM documents WHERE id = :documentId")
    suspend fun delete(documentId: Int)

    @Query("""
        UPDATE documents
            SET options = :options
            WHERE id = :documentId
    """)
    suspend fun updateOptions(documentId: Int, options: JsonElement)

    @Query("""
        UPDATE documents
            SET data = :data
            WHERE id = :documentId
    """)
    suspend fun updateData(documentId: Int, data: JsonElement)
}