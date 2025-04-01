package ru.rcfh.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.rcfh.database.entity.StateCacheEntity

@Dao
interface StateCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateState(state: StateCacheEntity)

    @Query("SELECT * FROM cache_state")
    suspend fun getAllStateCache(): List<StateCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(states: List<StateCacheEntity>)

    @Query("SELECT * FROM cache_state WHERE draft_id = :draftId AND path LIKE :path || '%' LIMIT 1")
    fun observeState(draftId: String, path: String): Flow<StateCacheEntity?>

    @Query("SELECT * FROM cache_state WHERE draft_id = :draftId AND path LIKE :path || '%'")
    fun observeTable(draftId: String, path: String): Flow<List<StateCacheEntity>>
}