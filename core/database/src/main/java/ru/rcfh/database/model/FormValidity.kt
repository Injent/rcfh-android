package ru.rcfh.database.model

import androidx.room.ColumnInfo

data class FormValidity(
    @ColumnInfo("form_id") val id: Int,
    @ColumnInfo("is_valid") val isValid: Boolean
)