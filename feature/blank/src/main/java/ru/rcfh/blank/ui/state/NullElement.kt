package ru.rcfh.blank.ui.state

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import java.util.Objects

data class NullElement(
    override val documentScope: DocumentScope
) : Element {

    override fun encodeToJsonElement(): JsonElement {
        return JsonNull
    }

    override fun contentHashCode(): Int {
        return Objects.hash("NullElement")
    }

    override fun toString(): String {
        return "null"
    }
}