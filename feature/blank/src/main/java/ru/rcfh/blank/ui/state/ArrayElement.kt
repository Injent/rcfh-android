package ru.rcfh.blank.ui.state

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import java.util.function.Predicate

data class ArrayElement(
    private val delegate: SnapshotStateList<Element> = mutableStateListOf(),
    override val documentScope: DocumentScope
) : Element, MutableList<Element> by delegate {

    override fun encodeToJsonElement(): JsonElement {
        return JsonArray(delegate.map(Element::encodeToJsonElement))
    }

    override fun toString(): String {
        val elements = joinToString(", ") { it.toString() }
        return "[$elements]"
    }

    override fun add(element: Element): Boolean {
        return delegate.add(element).also { notifyChangeAsync() }
    }

    override fun add(index: Int, element: Element) {
        delegate.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<Element>): Boolean {
        return delegate.addAll(index, elements).also { notifyChangeAsync() }
    }

    override fun addAll(elements: Collection<Element>): Boolean {
        return delegate.addAll(elements).also { notifyChangeAsync() }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun addFirst(e: Element) {
        delegate.addFirst(e)
        notifyChangeAsync()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun addLast(e: Element) {
        delegate.addLast(e)
        notifyChangeAsync()
    }

    override fun clear() {
        delegate.clear()
        notifyChangeAsync()
    }

    override fun remove(element: Element): Boolean {
        return delegate.remove(element).also { notifyChangeAsync() }
    }

    override fun removeAll(elements: Collection<Element>): Boolean {
        return delegate.removeAll(elements).also { notifyChangeAsync() }
    }

    override fun removeAt(index: Int): Element {
        return delegate.removeAt(index).also { notifyChangeAsync() }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun removeFirst(): Element {
        return delegate.removeFirst().also { notifyChangeAsync() }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun removeLast(): Element {
        return delegate.removeLast().also { notifyChangeAsync() }
    }

    override fun retainAll(elements: Collection<Element>): Boolean {
        return delegate.retainAll(elements).also { notifyChangeAsync() }
    }

    override fun set(index: Int, element: Element): Element {
        return delegate.set(index, element).also { notifyChangeAsync() }
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Element> {
        return delegate.subList(fromIndex, toIndex).also { notifyChangeAsync() }
    }

    override fun removeIf(filter: Predicate<in Element>): Boolean {
        return delegate.removeIf(filter).also { notifyChangeAsync() }
    }

    override fun contentHashCode(): Int {
        var result = "ArrayElement".hashCode()
        result = 31 * result + this.delegate.fold(0) { acc, element ->
            31 * acc + element.contentHashCode()
        }
        return result
    }

    private fun notifyChangeAsync() {
        documentScope.onChange()
    }
}