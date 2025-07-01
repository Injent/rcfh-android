package ru.rcfh.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import ru.rcfh.database.entity.FormEntity
import ru.rcfh.database.model.FormValidity

@Dao
interface FormDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(forms: List<FormEntity>)

    @Upsert
    suspend fun upsert(form: FormEntity)

    @Query("SELECT * FROM forms WHERE document_id = :documentId AND form_id = :formId LIMIT 1")
    suspend fun getForm(documentId: Int, formId: Int): FormEntity?

    @Query("SELECT * FROM forms WHERE document_id = :documentId")
    suspend fun getForms(documentId: Int): List<FormEntity>

    @Query("DELETE FROM forms WHERE document_id = :documentId")
    suspend fun deleteByDocumentId(documentId: Int)

    @Query("SELECT * FROM forms WHERE document_id = :documentId")
    suspend fun getFormsValidity(documentId: Int): List<FormValidity>
}