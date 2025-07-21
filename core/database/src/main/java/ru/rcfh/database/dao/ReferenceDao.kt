package ru.rcfh.database.dao

import androidx.room.Dao
import androidx.room.Query
import ru.rcfh.database.entity.ReferenceEntity
import ru.rcfh.database.entity.ReferenceWithMatchInfo

private const val RESULT_SIZE = 300

@Dao
interface ReferenceDao {
    @Query("""
        SELECT refs.*, matchinfo(refs_fts) as matchInfo
        FROM refs
        JOIN refs_fts ON refs.rowid = refs_fts.rowid
        WHERE refs_fts MATCH :query 
            AND refs.handbook_id = :handbookId
            AND (:codes IS NULL OR refs.code IN (:codes))
        LIMIT $RESULT_SIZE
    """)
    suspend fun search(handbookId: Int, query: String, codes: List<Int>?): List<ReferenceWithMatchInfo>

    @Query("""
        SELECT *
        FROM refs
        WHERE handbook_id = :handbookId
        AND refs.code IN (:codes)
        LIMIT $RESULT_SIZE
    """)
    suspend fun getRefs(handbookId: Int, codes: List<Int>): List<ReferenceEntity>

    @Query("""
        SELECT *
        FROM refs
        WHERE handbook_id = :handbookId
        LIMIT $RESULT_SIZE
    """)
    suspend fun getRefs(handbookId: Int): List<ReferenceEntity>

    @Query("SELECT * FROM refs WHERE handbook_id = :handbookId AND id = :id LIMIT 1")
    suspend fun getReference(handbookId: Int, id: Int): ReferenceEntity?

    @Query("DELETE FROM refs")
    suspend fun clear()
}