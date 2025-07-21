package ru.rcfh.core.sdui.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.ErrorAddress
import ru.rcfh.core.sdui.common.IndexAware
import ru.rcfh.core.sdui.common.IndexAwareStateList

@Stable
class RepeatableState(
    override val id: String,
    val name: String,
    private val emptyTemplate: () -> SnapshotStateList<FieldState>,
    val maxEntries: Int,
    documentState: DocumentState,
    initialValue: List<List<FieldState>> = emptyList(),
) : FieldState(documentState), IndexAware, Container {
    var enabled by mutableStateOf(true)
    val groups = initialValue.map {
        IndexAwareStateList(it.toMutableStateList())
    }.toMutableStateList()
    val canAddGroups by derivedStateOf {
        groups.size < maxEntries
    }

    fun addGroup() {
        groups.add(IndexAwareStateList(emptyTemplate()))
    }

    fun removeGroup(index: Int) {
        groups.removeAt(index)
    }

    override val mIndex: Int
        get() = -1

    override fun updateIndex(index: Int) {

    }

    override fun items(): List<FieldState> {
        return groups.flatten()
    }

    override fun isValid() = groups.all { group ->
        group.all { it.isValid() }
    }

    override fun detectErrors(): List<DetectedError> {
        return groups.flatMapIndexed { rowIndex, textStates ->
            textStates.flatMap { field ->
                field.detectErrors().map {
                    it.copy(
                        address = ErrorAddress(
                            parentId = id,
                            rowIndex = rowIndex
                        )
                    )
                }
            }
        }
    }

    override fun save(): JsonElement {
        return buildJsonArray {
            groups.forEach { group ->
                addJsonObject {
                    group.forEach { state ->
                        put(state.id, state.save())
                    }
                }
            }
        }
    }
}
