package ru.rcfh.core.sdui.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import ru.rcfh.core.sdui.common.ComputeMetadata
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.ErrorType
import ru.rcfh.core.sdui.common.Format
import ru.rcfh.core.sdui.common.IndexAware
import ru.rcfh.core.sdui.common.PostInitListener
import ru.rcfh.core.sdui.common.RefDependency
import ru.rcfh.core.sdui.common.Rule
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.event.CustomEvent
import ru.rcfh.core.sdui.event.SetPlus
import ru.rcfh.core.sdui.event.SetVariable
import ru.rcfh.core.sdui.event.SetVnum

@Stable
class TextState(
    override val id: String,
    val label: String,
    val hint: String? = null,
    val visual: Visual,
    val format: Format,
    documentState: DocumentState,
    rules: List<Rule> = emptyList(),
    initialValue: String = "",
    rowIndex: Int = -1,
    isEnabled: Boolean = true,
    private val metadata: ComputeMetadata? = null,
) : FieldState(documentState), IndexAware, PostInitListener {
    var refDependency by mutableStateOf<RefDependency?>(null)
        private set
    var rowIndex by mutableIntStateOf(rowIndex)
    private val rules = (rules + DotRule).toImmutableList()
    var error by mutableStateOf<String?>(null)
        private set
    var enabled by mutableStateOf(isEnabled)

    private var _value by mutableStateOf(initialValue)
    var value: String
        get() = _value
        set(value) {
            setValue(value)
        }

    init {
        if (id == "vnum") {
            document.observeEvent(SetVnum::class) { event ->
                value = event.value
            }
        }

        document.observeEvent(SetPlus::class) { event ->
            if (event.rowIndex == rowIndex && metadata?.inTable == event.tableId) {
                if (id.startsWith("dola") || id.startsWith("yarus")
                    || id.startsWith("kodporoda")) return@observeEvent
                enabled = !event.isSet
            }
        }

        if (document.initialized) onInitialized()

        if (id == "lvo") {
            document.observeEvent(CustomEvent::class) { event ->
                if (event.key != "srf") return@observeEvent

                
            }
        }
    }

    fun getParentDependency(): RefDependency? {
        return if (visual is Visual.Reference && visual.dependsOn != null) {
            document.findById<TextState>(
                templateId = visual.dependsOn,
                rowIndex = rowIndex
            )?.refDependency
        } else null
    }

    override fun onInitialized() {
        if (_value == "+") {
            // Fire event that disables fields in other tables
            setValue("+")
        }

        val s = if (visual is Visual.Number) {
            value.replace(',', '.')
        } else value
        hasErrors(s)
    }

    fun setReference(refDependency: RefDependency?, value: String) {
        this.refDependency = refDependency
        this.value = value

    }

    @JvmName("setVal")
    private fun setValue(value: String) {
        if (!enabled) return

        if (visual is Visual.Decimal && visual.canSetPlus) {
            document.postEvent(
                SetPlus(
                    tableId = metadata?.inTable ?: "",
                    rowIndex = rowIndex,
                    isSet = value == "+"
                )
            )
        }

        val s = if (visual is Visual.Number) {
            value.replace(',', '.')
        } else value
        if (!hasErrors(s)) {
            _value = s
            document.postEvent(
                SetVariable(
                    templateId = id,
                    rowIndex = rowIndex,
                    value = s,
                    inGroup = metadata?.inGroup
                )
            )
        }
    }

    private fun hasErrors(s: String): Boolean {
        if (!s.isValid(visual)) { return true }

        val strictInputSatisfied = rules
            .filterIsInstance<Rule.StrictInput>()
            .all { it.isMet(s) }
        if (!strictInputSatisfied) return true

        val failedRule = rules.firstOrNull { rule ->
            !rule.isMet(s)
        }

        if (format is Format.FullnessLimits) {
            val fullnessLimit = document.findById<TextState>(
                templateId = format.fieldId
            )?.value?.toFloatOrNull() ?: 0f

            if ((s.toFloatOrNull() ?: 0f) > fullnessLimit) {
                error = format.errorMsg
                return false
            }
        }

        error = failedRule?.message
        return false
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
        return when (visual) {
            is Visual.Reference -> {
                refDependency?.let { dependency ->
                    buildJsonObject {
                        put("handbookId", JsonPrimitive(dependency.handbookId))
                        put("refId", JsonPrimitive(dependency.refId))
                        put("value", JsonPrimitive(value))
                    }
                } ?: JsonNull
            }
            else -> JsonPrimitive(value)
        }
    }

    override fun toString(): String {
        return "TextState(value=$value,rowIndex=$rowIndex)"
    }
}

private val DotRule = Rule.Regex("^(?:.*[^.])\$|^\$".toRegex(), "Ошибка")

private fun String.isValid(visual: Visual): Boolean {
    if (isEmpty()) return true
    if (visual is Visual.Decimal && this == "+") return true
    if ((visual is Visual.Decimal || visual is Visual.Number) && contains(' ')) {
        return false
    }
    val regex = when (visual) {
        is Visual.Decimal -> "^(?:\$|(-?[1-9]\\d*|-?0(?!\\d))\$)"
        is Visual.Number -> "^-?(0(\\.[0-9]*)?|[1-9][0-9]*(\\.[0-9]+)?|[1-9][0-9]*\\.)\$"
        else -> return true
    }.toRegex()
    return regex.matches(this)
}