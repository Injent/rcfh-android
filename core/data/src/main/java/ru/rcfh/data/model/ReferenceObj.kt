package ru.rcfh.data.model

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

data class ReferenceObj(
    val jsonObject: JsonObject
) {
    val id: Int
        get() = requireNotNull(jsonObject["id"]).jsonPrimitive.int
    val name: String
        get() = requireNotNull(jsonObject["name"]).jsonPrimitive.content
    val description: String?
        get() = jsonObject["description"]?.jsonPrimitive?.content
    val parentId: Int?
        get() = jsonObject["parent_id"]?.jsonPrimitive?.intOrNull
}
