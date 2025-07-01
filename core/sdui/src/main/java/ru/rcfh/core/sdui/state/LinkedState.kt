package ru.rcfh.core.sdui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.IndexAware
import ru.rcfh.core.sdui.event.SetVariable

class LinkedState(
    override val id: String,
    val label: String,
    initialValue: String,
    rowIndex: Int,
    documentState: DocumentState
) : FieldState(documentState), IndexAware {
    var rowIndex by mutableIntStateOf(rowIndex)
        private set
    var value by mutableStateOf(initialValue)
        private set

    init {
        document.observeEvent(SetVariable::class) { event ->
            if (event.rowIndex == rowIndex && event.templateId == id) {
                value = event.value
            }
        }
    }

    fun relink() {
        document.findById<TextState>(
            templateId = id,
            rowIndex = rowIndex
        )
            ?.let { state ->
                value = state.value
            }
    }

    override val mIndex: Int
        get() = this.rowIndex

    override fun updateIndex(index: Int) {
        rowIndex = index
    }

    override fun isValid() = true

    override fun detectErrors(): List<DetectedError> = emptyList()

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun toString(): String {
        return "LinkedState(value=$value,rowIndex=$rowIndex)"
    }
}