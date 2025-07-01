package ru.rcfh.core.sdui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import ru.rcfh.core.sdui.common.ComputeMetadata
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.Formula
import ru.rcfh.core.sdui.common.IndexAware
import ru.rcfh.core.sdui.event.SetVariable
import ru.rcfh.core.sdui.util.evaluateSimpleFormula

class CalculatedState(
    override val id: String,
    val label: String,
    val unit: String,
    val formula: Formula,
    documentState: DocumentState,
    initialValue: String = "",
    rowIndex: Int,
    private val metadata: ComputeMetadata?
) : FieldState(documentState), IndexAware {
    var rowIndex by mutableIntStateOf(rowIndex)

    private var _value by mutableStateOf(initialValue)

    var value: String
        get() = _value
        private set(value) {
            _value = value
        }

    private val ids = extractStringsInBraces(formula.value).toImmutableList()

    init {
        document.observeEvent(SetVariable::class) { event ->
            if (event.templateId in ids && event.rowIndex == rowIndex) {
                calculate()
            }
        }
    }

    fun calculate() {
        if (formula.isProductFormula() && metadata != null) {
            document.findGroupById(metadata.inGroup).map {
                it.groups.getOrNull()
            }
        } else {
            val a = ids.associateWith { id ->
                document.findById<TextState>(
                    templateId = id,
                    rowIndex = rowIndex
                )?.value?.toFloatOrNull()
            }
                .filterValues { it != null }
                .let { it as Map<String, Float> }

            value = evaluateSimpleFormula(formula.value, a).toString()
        }
    }

    override val mIndex: Int
        get() = this.rowIndex

    override fun updateIndex(index: Int) {
        rowIndex = index
    }

    override fun isValid() = true

    override fun toString(): String {
        return "CalculateState(value=$_value)"
    }

    override fun detectErrors(): List<DetectedError> = emptyList()

    override fun save(): JsonElement {
        return JsonPrimitive(_value)
    }

    private fun extractStringsInBraces(input: String): List<String> {
        val regex = "\\{([^}]+)\\}".toRegex()
        return regex.findAll(input).map { it.groupValues[1] }.toList()
    }
}