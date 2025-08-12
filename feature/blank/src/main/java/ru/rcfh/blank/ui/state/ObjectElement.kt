package ru.rcfh.blank.ui.state

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.util.Objects
import java.util.function.BiFunction
import java.util.function.Function

data class ObjectElement(
    private val delegate: SnapshotStateMap<String, Element> = mutableStateMapOf(),
    override val documentScope: DocumentScope
) : Element, MutableMap<String, Element> by delegate {
    override fun encodeToJsonElement(): JsonElement {
        return JsonObject(delegate.mapValues { (_, value) -> value.encodeToJsonElement() })
    }

    override fun toString(): String {
        val entries = entries.joinToString(", ") { "\"${it.key}\": ${it.value}" }
        return "{$entries}"
    }

    override fun remove(key: String, value: Element): Boolean {
        return delegate.remove(key, value).also { notifyChangeAsync() }
    }

    override fun compute(
        key: String,
        remappingFunction: BiFunction<in String, in Element?, out Element?>
    ): Element? {
        return delegate.compute(key, remappingFunction).also { notifyChangeAsync() }
    }

    override fun computeIfAbsent(
        key: String,
        mappingFunction: Function<in String, out Element>
    ): Element {
        return delegate.computeIfAbsent(key, mappingFunction).also { notifyChangeAsync() }
    }

    override fun computeIfPresent(
        key: String,
        remappingFunction: BiFunction<in String, in Element, out Element?>
    ): Element? {
        return delegate.computeIfPresent(key, remappingFunction).also { notifyChangeAsync() }
    }

    override fun merge(
        key: String,
        value: Element,
        remappingFunction: BiFunction<in Element, in Element, out Element?>
    ): Element? {
        return delegate.merge(key, value, remappingFunction).also { notifyChangeAsync() }
    }

    override fun putIfAbsent(key: String, value: Element): Element? {
        return delegate.putIfAbsent(key, value).also { notifyChangeAsync() }
    }

    override fun replace(key: String, oldValue: Element, newValue: Element): Boolean {
        return delegate.replace(key, oldValue, newValue).also { notifyChangeAsync() }
    }

    override fun replace(key: String, value: Element): Element? {
        return delegate.replace(key, value).also { notifyChangeAsync() }
    }

    override fun replaceAll(function: BiFunction<in String, in Element, out Element>) {
        delegate.replaceAll(function)
        notifyChangeAsync()
    }

    override fun clear() {
        delegate.clear()
        notifyChangeAsync()
    }

    override fun put(key: String, value: Element): Element? {
        return delegate.put(key, value).also { notifyChangeAsync() }
    }

    override fun putAll(from: Map<out String, Element>) {
        delegate.putAll(from)
        notifyChangeAsync()
    }

    override fun remove(key: String): Element? {
        return delegate.remove(key).also { notifyChangeAsync() }
    }

    override fun contentHashCode(): Int {
        var result = "ObjectElement".hashCode()
        result = 31 * result + this.delegate.entries.fold(0) { acc, entry ->
            val entryHash = Objects.hash(entry.key, entry.value.contentHashCode())
            31 * acc + entryHash
        }
        return result
    }

    private fun notifyChangeAsync() {
        documentScope.onChange()
    }
}