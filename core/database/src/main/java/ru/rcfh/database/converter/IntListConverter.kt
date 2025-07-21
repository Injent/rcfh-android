package ru.rcfh.database.converter

import androidx.room.TypeConverter

object IntListConverter {
    @TypeConverter
    fun stringToIntList(value: String?): List<Int>? {
        return value?.split(',')?.mapNotNull(String::toIntOrNull) ?: return null
    }

    @TypeConverter
    fun intListToString(list: List<Int>?): String? {
        return list?.joinToString(separator = ",") ?: return null
    }
}