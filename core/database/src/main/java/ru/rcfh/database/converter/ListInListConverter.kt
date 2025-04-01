package ru.rcfh.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ListInListConverter {
    private val format = Json

    @TypeConverter
    fun listInListToJson(listInList: List<List<String>>): String {
        return format.encodeToString(listInList)
    }

    @TypeConverter
    fun jsonToListInList(json: String): List<List<String>> {
        return try {
            format.decodeFromString(json)
        } catch (e: SerializationException) {
            emptyList()
        }
    }
}