package ru.rcfh.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object LocalDateTimeConverter {

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun localDateTimeToSeconds(localDateTime: LocalDateTime): Long {
        return localDateTime.toInstant(TimeZone.currentSystemDefault()).epochSeconds
    }

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun secondsToLocalTimeDate(seconds: Long): LocalDateTime {
        return Instant.fromEpochSeconds(seconds).toLocalDateTime(TimeZone.currentSystemDefault())
    }
}