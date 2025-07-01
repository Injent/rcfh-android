package ru.rcfh.core.sdui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import ru.rcfh.core.sdui.state.FieldState

class Row(
    private val delegate: SnapshotStateList<FieldState>
) : IndexAware, MutableList<FieldState> by delegate {
    private var rowIndex by mutableIntStateOf(-1)

    override val mIndex: Int
        get() = rowIndex

    override fun updateIndex(index: Int) {
        rowIndex = index
        delegate.filterIsInstance<IndexAware>().forEach { it.updateIndex(index) }
    }

    override fun toString(): String {
        return delegate.toString()
    }
}