package ru.rcfh.blank.ui.state

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import java.util.Objects

data class TextElement(
    val content: String = "",
    override val documentScope: DocumentScope
) : Element {

    override fun encodeToJsonElement(): JsonElement {
        return JsonPrimitive(content)
    }

    override fun contentHashCode(): Int {
        return Objects.hash("TextElement", this.content)
    }

    override fun toString(): String {
        return content
    }
}