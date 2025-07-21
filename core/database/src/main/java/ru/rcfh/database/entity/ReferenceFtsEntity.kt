package ru.rcfh.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

/**
  FTS4 table for full-text search on ReferenceEntity's name and description fields.
  Uses UNICODE61 tokenizer for case-insensitive search and broad language support,
  including Russian text. The remove_diacritics=1 argument normalizes diacritical marks
  (e.g., "é" to "e") to simplify matching, suitable for multilingual data where
  diacritics are not critical.
 **/
@Fts4(
    contentEntity = ReferenceEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics=1"]
)
@Entity(tableName = "refs_fts")
data class ReferenceFtsEntity(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?
)