package ru.rcfh.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class NetworkHandbookCollection(
    @SerialName("version")
    val version: Int,
    @SerialName("handbooks")
    val handbooks: List<Handbook>
) {
    @Serializable
    data class Handbook(
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String,
        @SerialName("references")
        val references: List<JsonObject>
    )
}