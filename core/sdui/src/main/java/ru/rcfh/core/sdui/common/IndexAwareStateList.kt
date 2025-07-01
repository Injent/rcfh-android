package ru.rcfh.core.sdui.common

import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.function.Predicate

class IndexAwareStateList<T>(
    private val delegate: SnapshotStateList<T>,
) : MutableList<T> by delegate {
    override fun add(element: T): Boolean {
        return delegate.add(element).also {
            updateIndices()
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        return delegate.addAll(elements).also { updateIndices() }
    }

    override fun add(index: Int, element: T) {
        return delegate.add(index, element).also { updateIndices() }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return delegate.addAll(index, elements).also { updateIndices() }
    }

    override fun clear() {
        delegate.clear()
        updateIndices()
    }

    override fun remove(element: T): Boolean {
        return delegate.remove(element).also { updateIndices() }
    }

    override fun removeIf(filter: Predicate<in T>): Boolean {
        return delegate.removeIf(filter).also { updateIndices() }
    }

    override fun removeAt(index: Int): T {
        return delegate.removeAt(index).also { updateIndices() }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return delegate.removeAll(elements).also { updateIndices() }
    }

    override fun toString(): String {
        return delegate.toString()
    }

    private fun updateIndices() {
        delegate.forEachIndexed { index, item ->
            if (item is IndexAware) item.updateIndex(index)
        }
    }
}