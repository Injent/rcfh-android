package ru.rcfh.database.dao

import androidx.room.Dao
import androidx.room.Query
import ru.rcfh.database.entity.ReferenceEntity

@Dao
interface ReferenceDao {
    @Query("""
        SELECT * FROM `references`
        WHERE 
            CASE 
                WHEN :query = '' THEN 1 
                ELSE name LIKE '%' || :query || '%' 
            END
            AND handbook_id = :handbookId
        ORDER BY name
        LIMIT :size;
    """)
    suspend fun search(handbookId: Int, query: String, size: Int = 50): List<ReferenceEntity>
}