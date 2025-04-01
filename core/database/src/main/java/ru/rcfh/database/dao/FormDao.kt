package ru.rcfh.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.rcfh.database.entity.FormEntity

@Dao
interface FormDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(forms: List<FormEntity>)

    @Query("SELECT * FROM forms WHERE id = :id LIMIT 1")
    suspend fun getForm(id: Int): FormEntity?
}