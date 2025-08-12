package ru.rcfh.blank.ui.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

sealed interface Element {

    val textOrEmpty: String
        get() = (this as? TextElement)?.content ?: ""

    val doubleOrZero: Double
        get() = (this as? TextElement)?.content?.toDoubleOrNull() ?: 0.0

    val intOrNull: Int?
        get() = (this as? TextElement)?.content?.toIntOrNull()

    val listOrEmpty: List<Element>
        get() = (this as? ArrayElement) ?: emptyList()

    val array: ArrayElement?
        get() = this as? ArrayElement

    val documentScope: DocumentScope

    fun encodeToJsonElement(): JsonElement

    fun contentHashCode(): Int

    companion object {
        fun decodeFromJsonElement(jsonElement: JsonElement, documentScope: DocumentScope): Element {
            return when (jsonElement) {
                is JsonArray -> {
                    jsonElement
                        .map { decodeFromJsonElement(it, documentScope) }
                        .toTypedArray()
                        .let { ArrayElement(mutableStateListOf(*it), documentScope) }
                }
                is JsonObject -> {
                    jsonElement
                        .map { (key, value) ->
                            key to decodeFromJsonElement(value, documentScope)
                        }
                        .toTypedArray()
                        .let { ObjectElement(mutableStateMapOf(*it), documentScope) }
                }
                is JsonPrimitive -> TextElement(
                    content = jsonElement.contentOrNull ?: "",
                    documentScope = documentScope
                )
                JsonNull -> NullElement(documentScope = documentScope)
            }
        }
    }
}

inline fun <reified T : Element> Element.create(): T {
    return when (T::class) {
        ArrayElement::class -> ArrayElement(documentScope = documentScope)
        ObjectElement::class -> ObjectElement(documentScope = documentScope)
        TextElement::class -> TextElement(documentScope = documentScope)
        NullElement::class -> NullElement(documentScope = documentScope)
        else -> error("Invalid type")
    } as T
}