package ru.rcfh.core.sdui.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.ErrorAddress
import ru.rcfh.core.sdui.common.IndexAwareStateList
import ru.rcfh.core.sdui.common.Row
import ru.rcfh.core.sdui.event.AddPage
import ru.rcfh.core.sdui.event.RemovePage
import ru.rcfh.core.sdui.event.SetPlus

data class Total(
    val name: String,
    val row: List<Double>
)

@Stable
class TableState(
    override val id: String,
    val name: String,
    private val emptyTemplate: (Int) -> SnapshotStateList<FieldState>,
    val total: ImmutableMap<String, CalculatedState>,
    documentState: DocumentState,
    val dependency: String? = null,
    initialValue: List<List<FieldState>>,
    triggerIds: List<String>,
): FieldState(documentState), Container {
    val columnsCount = emptyTemplate(-1).size
    val rows = IndexAwareStateList(initialValue.map {
        Row(
            delegate = it.toMutableStateList()
        )
    }.toMutableStateList())
    var totals by mutableStateOf(emptyList<Total>())

    init {
        document.observeEvent(AddPage::class) { event ->
            if (event.templateId == dependency) {
                addRow()
            }
        }
        document.observeEvent(RemovePage::class) { event ->
            if (event.templateId == dependency) {
                removeRow(event.rowIndex)
            }
        }
        document.observeEvent(SetPlus::class) { event ->
            if (event.tableId == dependency) {
                rows.getOrNull(event.rowIndex)?.forEach { field ->
                    when (field) {
                        is RatioState -> {
                            field.values.forEach { it.enabled = !event.isSet }
                        }
                        is TextState -> field.enabled = !event.isSet
                        is RepeatableState -> field.enabled = !event.isSet
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun items(): List<FieldState> {
        return rows.flatten() + total.values
    }

    private fun addRow() {
        rows.add(Row(emptyTemplate(rows.size)))
    }

    private fun removeRow(index: Int) {
        rows.removeAt(index)
    }

    override fun isValid() = rows.all { row ->
        row.all { it.isValid() }
    }

    override fun detectErrors(): List<DetectedError> {
        return rows.flatMapIndexed { rowIndex, textStates ->
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
        return buildJsonObject {
            putJsonArray("values") {
                rows.forEach { row ->
                    addJsonObject {
                        row.forEach { state ->
                            put(state.id, state.save())
                        }
                    }
                }
            }
            putJsonObject("total") {
                total.forEach { (_, state) ->
                    put(state.id, state.save())
                }
            }
        }
    }
}

fun Row.toMap(): Map<String, String?> {
    val list = mutableMapOf<String, String?>()

    forEach { item ->
        when (item) {
            is Container -> item.items().forEach {
                list[it.id] = it.getValue()
            }
            else -> list[item.id] = item.getValue()
        }
    }
    return list
}

fun Row.getValue(id: String): String? {
    for (item in this) {
        when (item) {
            is CalculatedState -> if (item.id == id) return item.value
            is LinkedState -> if (item.id == id) return item.value
            is TextState -> if (item.id == id) return item.value
            is Container -> item.items().find { it.id == id }?.let {
                when (it) {
                    is CalculatedState -> return it.value
                    is TextState -> return it.value
                    is LinkedState -> return it.value
                    else -> Unit
                }
            }
            else -> Unit
        }
    }
    return null
}

fun FieldState.getValue(): String? {
    return when (this) {
        is CalculatedState -> value
        is LinkedState -> value
        is TextState -> value
        else -> null
    }
}