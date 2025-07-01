package ru.rcfh.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object LocalDateTimeConverter {
    @TypeConverter
    fun localDateTimeToSeconds(localDateTime: LocalDateTime): Long {
        return localDateTime.toInstant(TimeZone.currentSystemDefault()).epochSeconds
    }
    @TypeConverter
    fun secondsToLocalTimeDate(seconds: Long): LocalDateTime {
        return Instant.fromEpochSeconds(seconds).toLocalDateTime(TimeZone.currentSystemDefault())
    }
}