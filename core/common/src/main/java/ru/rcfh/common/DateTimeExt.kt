package ru.rcfh.common

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDate.Companion.now(): LocalDate {
    return LocalDateTime.now().date
}