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
import ru.rcfh.core.sdui.common.PostInitListener
import ru.rcfh.core.sdui.event.SetVariable
import ru.rcfh.core.sdui.util.evaluateDotProductFormula
import ru.rcfh.core.sdui.util.evaluateSimpleFormula
import ru.rcfh.core.sdui.util.format

class CalculatedState(
    override val id: String,
    val label: String,
    val unit: String,
    val formula: Formula,
    documentState: DocumentState,
    initialValue: String = "",
    rowIndex: Int,
    private val metadata: ComputeMetadata?
) : FieldState(documentState), IndexAware, PostInitListener {
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
            if (formula.isProductFormula()) {
                if (event.templateId in ids) {
                    calculate()
                }
            } else {
                if ((event.templateId in ids && event.rowIndex == rowIndex) || "$${event.templateId}" in ids) {
                    calculate()
                }
            }
        }
    }

    override fun onInitialized() {
        calculate()
    }

    private fun calculate() {
        if (formula.isProductFormula() && metadata != null && ids.size == 2) {
            val table = document.findById<TableState>(templateId = metadata.inTable) ?: return
            val map = ids.associateWith { fieldId ->
                table.rows
                    .map { row ->
                        val top = when (val state = row.find { it.id == fieldId }) {
                            is LinkedState -> state.value
                            is TextState -> state.value
                            is CalculatedState -> state.value
                            else -> null
                        }?.toFloatOrNull()

                        val deep = row.find {
                            it is RatioState && fieldId in it.values.map { it.id }
                        }
                            ?.let {
                                (it as RatioState).values.find { it.id == fieldId }?.value?.toFloatOrNull()
                            }

                        top ?: deep
                    }
            }

            value = runCatching {
                evaluateDotProductFormula(
                    a = map[ids[0]]!!,
                    b = map[ids[1]]!!
                ).format(2)
            }.getOrDefault("ОШИБКА")
        } else {
            val a = ids.associateWith { id ->
                if (id.startsWith("$")) {
                    val foundState = document.findById<FieldState>(
                        templateId = id.drop(1),
                    )
                    when (foundState) {
                        is TextState -> foundState.value.toFloatOrNull()
                        is CalculatedState -> foundState.value.toFloatOrNull()
                        else -> null
                    }
                } else {
                    document.findById<TextState>(
                        templateId = id,
                        rowIndex = rowIndex
                    )?.value?.toFloatOrNull()
                }
            }
                .filterValues { it != null }
                .let { it as Map<String, Float> }

            value = try {
                evaluateSimpleFormula(formula.value, a).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                "ОШИБКА"
            }
            document.postEvent(SetVariable(templateId = id, rowIndex = rowIndex, value = value))
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