package ru.rcfh.core.sdui.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull

sealed interface StateElement {
    companion object

    object Saver : androidx.compose.runtime.saveable.Saver<StateElement, String> {
        override fun restore(value: String): StateElement {
            return StateElement.parse(value)
        }

        override fun SaverScope.save(value: StateElement): String {
            return Json.encodeToString(value.toJsonElement())
        }
    }
}

data class StateObject(
    val delegate: SnapshotStateMap<String, StateElement> = mutableStateMapOf()
) : StateElement, MutableMap<String, StateElement> by delegate {
    override fun toString(): String {
        val entries = entries.joinToString(", ") { "\"${it.key}\": ${it.value}" }
        return "{$entries}"
    }
}

data class StateList(
    val delegate: SnapshotStateList<StateElement> = mutableStateListOf()
) : StateElement, MutableList<StateElement> by delegate {
    override fun toString(): String {
        val elements = joinToString(", ") { it.toString() }
        return "[$elements]"
    }
}

data class StateText(
    val content: String,
) : StateElement {
    override fun toString(): String = "\"$content\""
}

data object StateNull : StateElement {
    override fun toString(): String = "null"
}

fun StateElement.Companion.parse(jsonString: String): StateElement {
    val jsonElement = Json.parseToJsonElement(jsonString)
    return fromJsonElement(jsonElement)
}

operator fun StateElement?.set(key: String, value: String) {
    (this as? StateObject)?.put(key, StateText(value))
}

operator fun StateElement?.get(index: Int): StateElement? {
    return (this as? StateList)?.get(index)
}

operator fun StateElement?.get(key: String): StateElement? {
    return (this as? StateObject)?.get(key)
}

val StateElement?.obj: StateObject?
    get() = this as? StateObject?

val StateElement?.list: StateList?
    get() = this as? StateList?

val StateElement?.content: String?
    get() = (this as? StateText)?.content

operator fun StateObject?.get(key: String): StateElement? = this?.get(key)

operator fun StateList?.get(index: Int): StateElement? = this?.get(index)

fun StateElement.Companion.fromJsonElement(element: JsonElement): StateElement = when (element) {
    is JsonObject -> {
        val result = StateObject()
        element.forEach { (key, value) ->
            result[key] = fromJsonElement(value)
        }
        result
    }
    is JsonArray -> {
        val result = StateList()
        element.forEach { item ->
            result.add(fromJsonElement(item))
        }
        result
    }
    is JsonNull -> StateNull
    is JsonPrimitive -> StateText(
        when {
            element.isString -> element.contentOrNull ?: ""
            else -> element.toString()
        }
    )
}

fun StateElement.toJsonElement(): JsonElement = when (this) {
    is StateObject -> {
        val map = mutableMapOf<String, JsonElement>()
        forEach { (key, json) ->
            map[key] = json.toJsonElement()
        }
        JsonObject(map)
    }
    is StateList -> {
        val list = map { it.toJsonElement() }
        JsonArray(list)
    }
    is StateText -> JsonPrimitive(content)
    is StateNull -> JsonNull
}

fun StateList?.addJsonObject(block: JsonObjectBuilder.() -> Unit) {
    this?.add(StateElement.fromJsonElement(buildJsonObject(block)))
}

fun JsonObjectBuilder.put(key: String, value: String) {
    this.put(key, JsonPrimitive(value))
}

fun StateList?.addList(block: JsonArrayBuilder.() -> Unit) {
    this?.add(StateElement.fromJsonElement(buildJsonArray(block)))
}

fun StateList?.add(value: String) {
    this?.add(StateText(value))
}

fun StateObject?.put(key: String, value: String) {
    this?.put(key, StateText(value))
}

fun StateObject?.putList(key: String, block: JsonArrayBuilder.() -> Unit) {
    this?.put(key, StateElement.fromJsonElement(buildJsonArray(block)))
}

fun StateObject?.putObject(key: String, block: JsonObjectBuilder.() -> Unit) {
    this?.put(key, StateElement.fromJsonElement(buildJsonObject(block)))
}

inline fun <reified T : StateElement> StateElement.getOrCreate(path: String, defaultState: T): T {
    if (path.isEmpty() || !path.startsWith("$")) return defaultState // Путь должен начинаться с $

    val parts = parseJsonPath(path)
    if (parts.isEmpty()) return defaultState // Пустой путь возвращает defaultState

    var current: StateElement = this

    for ((index, part) in parts.withIndex()) {
        val isLastPart = index == parts.lastIndex

        when {
            part.toIntOrNull() != null -> {
                val arrayIndex = part.toInt()
                val list = current as? StateList ?: return defaultState
                while (list.size <= arrayIndex) {
                    list.add(StateNull)
                }
                if (isLastPart) {
                    if (list[arrayIndex] == StateNull || list[arrayIndex] !is T) {
                        list[arrayIndex] = defaultState
                    }
                    return list[arrayIndex] as T
                } else if (list[arrayIndex] == StateNull || list[arrayIndex] is StateText) {
                    list[arrayIndex] = if (parts.getOrNull(index + 1)?.toIntOrNull() != null) StateList() else StateObject()
                }
                current = list[arrayIndex]
            }
            else -> {
                val obj = current as? StateObject ?: return defaultState
                if (isLastPart) {
                    if (!obj.containsKey(part) || obj[part] == StateNull || obj[part] !is T) {
                        obj[part] = defaultState
                    }
                    return obj[part] as T
                } else if (!obj.containsKey(part) || obj[part] is StateText || obj[part] == StateNull) {
                    obj[part] = if (parts.getOrNull(index + 1)?.toIntOrNull() != null) StateList() else StateObject()
                }
                current = obj[part]!!
            }
        }
    }

    return if (current is T) current else defaultState
}

fun StateObject.update(path: String, value: StateElement) {
    if (path.isEmpty() || !path.startsWith("$")) return // Путь должен начинаться с $

    val parts = parseJsonPath(path)
    if (parts.isEmpty()) return // Пустой путь после парсинга игнорируется

    val parentPath = parts.dropLast(1).joinToString(".")
    val lastPart = parts.last()

    val parent = if (parentPath.isNotEmpty()) {
        getOrDefault("$$parentPath", StateObject())
    } else {
        this
    }

    when {
        lastPart.toIntOrNull() != null -> {
            val index = lastPart.toInt()
            val list = parent as? StateList ?: return
            while (list.size <= index) {
                list.add(StateNull)
            }
            list[index] = value
        }
        else -> {
            val obj = parent as? StateObject ?: return
            obj[lastPart] = value
        }
    }
}

fun parseJsonPath(path: String): List<String> {
    if (path == "$") return emptyList()

    val parts = mutableListOf<String>()
    var current = StringBuilder()
    var i = 1 // Пропускаем $
    var inBracket = false

    while (i < path.length) {
        when (val c = path[i]) {
            '.' -> {
                if (!inBracket) {
                    if (current.isNotEmpty()) {
                        parts.add(current.toString())
                        current = StringBuilder()
                    }
                } else {
                    current.append(c)
                }
            }
            '[' -> {
                if (current.isNotEmpty()) {
                    parts.add(current.toString())
                    current = StringBuilder()
                }
                inBracket = true
            }
            ']' -> {
                if (inBracket && current.isNotEmpty()) {
                    parts.add(current.toString().trim('\'', '"'))
                    current = StringBuilder()
                    inBracket = false
                }
            }
            else -> current.append(c)
        }
        i++
    }
    if (current.isNotEmpty()) {
        parts.add(current.toString())
    }
    return parts
}