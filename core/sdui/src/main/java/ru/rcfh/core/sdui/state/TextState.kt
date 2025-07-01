package ru.rcfh.core.sdui.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.ErrorType
import ru.rcfh.core.sdui.common.IndexAware
import ru.rcfh.core.sdui.common.Rule
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.event.SetVariable

@Stable
class TextState(
    override val id: String,
    val label: String,
    val hint: String? = null,
    val visual: Visual,
    documentState: DocumentState,
    rules: List<Rule> = emptyList(),
    initialValue: String = "",
    rowIndex: Int = -1,
    isEnabled: Boolean = true
) : FieldState(documentState), IndexAware {
    var rowIndex by mutableIntStateOf(rowIndex)
    private val rules = (rules + DotRule).toImmutableList()
    var error by mutableStateOf<String?>(null)
        private set
    var enabled by mutableStateOf(isEnabled)

    private var _value by mutableStateOf(initialValue)
    var value: String
        get() = _value
        set(value) {
            sValue(value)
        }

    init {
        val s = value.replace(',', '.')
        testForErrors(s)
    }

    private fun sValue(value: String) {
        if (!enabled) return
        val s = value.replace(',', '.')
        testForErrors(s)
        _value = s
        document.postEvent(SetVariable(templateId = id, rowIndex = rowIndex, value = value))
    }

    private fun testForErrors(s: String) {
        if (!s.isValid(visual)) { return }

        val strictInputSatisfied = rules
            .filterIsInstance<Rule.StrictInput>()
            .all { it.isMet(s) }
        if (!strictInputSatisfied) return

        val failedRule = rules.firstOrNull { rule ->
            !rule.isMet(s)
        }
        error = failedRule?.message
    }

    override val mIndex: Int
        get() = this.rowIndex

    override fun updateIndex(index: Int) {
        rowIndex = index
    }

    override fun isValid() = error == null

    override fun detectErrors(): List<DetectedError> {
        return error?.let {
            listOf(
                DetectedError(
                    templateId = id,
                    name = label,
                    error = it,
                    type = if (_value.isBlank()) ErrorType.WARNING else ErrorType.SEVERE
                )
            )
        } ?: emptyList()
    }

    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun toString(): String {
        return "TextState(value=$value,rowIndex=$rowIndex)"
    }
}

private val DotRule = Rule.Regex("^(?:.*[^.])\$|^\$".toRegex(), "Ошибка")

private fun String.isValid(visual: Visual): Boolean {
    if (isEmpty()) return true
    val regex = when (visual) {
        is Visual.Decimal -> "^(?:\$|(-?[1-9]\\d*|-?0(?!\\d))\$)"
        is Visual.Number -> "^-?(0(\\.[0-9]*)?|[1-9][0-9]*(\\.[0-9]+)?|[1-9][0-9]*\\.)\$"
        else -> return true
    }.toRegex()
    return regex.matches(this)
}