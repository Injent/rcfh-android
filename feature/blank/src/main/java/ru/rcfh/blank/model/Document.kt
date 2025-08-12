package ru.rcfh.blank.model

import kotlinx.serialization.json.JsonElement

data class Document(
    val id: Long,
    val ownerId: Int,
    val name: String,
    val data: JsonElement
)
