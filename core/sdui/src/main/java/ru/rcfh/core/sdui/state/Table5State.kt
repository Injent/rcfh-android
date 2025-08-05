package ru.rcfh.core.sdui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.IndexAwareStateList
import ru.rcfh.core.sdui.common.Row
import ru.rcfh.core.sdui.event.SetVariable

class Table5State(
    override val id: String,
    val name: String,
    private val rowTemp: (Int) -> List<FieldState>,
    private val dependency: String?,
    document: DocumentState,
    initialValue: List<List<FieldState>>,
) : Container, FieldState(document = document) {
    private val triggerIds = rowTemp(-1).flatMap { field ->
        when (field) {
            is Container -> field.items().map(FieldState::id)
            else -> listOf(field.id)
        }
    }

    val rows = IndexAwareStateList(initialValue.map {
        Row(delegate = it.toMutableStateList())
    }.toMutableStateList())
    val colCount = rowTemp(-1).sumOf {
        when (it) {
            is RatioState -> it.values.size
            is RepeatableState -> 0
            else -> 1
        }
    }
    var totals by mutableStateOf(emptyList<YarusTotal>())

    init {
        document.observeEvent(SetVariable::class) { event ->
            if (event.templateId !in triggerIds) return@observeEvent


        }
    }

    override fun isValid(): Boolean {
        return true
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
        }
    }

    override fun detectErrors(): List<DetectedError> {
        TODO("Not yet implemented")
    }

    override fun items(): List<FieldState> {
        return rows.flatten()
    }
}