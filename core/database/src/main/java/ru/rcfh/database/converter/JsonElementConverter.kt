package ru.rcfh.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object JsonElementConverter {
    private val format = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun stringToJsonObject(value: String): JsonElement {
        return format.decodeFromString(value)
    }

    @TypeConverter
    fun jsonObjectToString(value: JsonElement): String {
        return format.encodeToString(value)
    }
}