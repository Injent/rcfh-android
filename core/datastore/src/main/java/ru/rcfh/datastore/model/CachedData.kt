package ru.rcfh.datastore.model

import arrow.optics.optics
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@optics
sealed interface CachedData {
    @Serializable
    @SerialName("plain")
    @optics
    data class Plain(
        val field: String,
        val value: String = ""
    ) : CachedData {
        companion object
    }

    @Serializable
    @SerialName("table")
    @optics
    data class Table(
        val tableId: Int,
        val rows: List<List<Plain>>
    ) : CachedData {
        companion object
    }

    companion object
}