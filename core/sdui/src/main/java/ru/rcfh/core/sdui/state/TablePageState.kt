package ru.rcfh.core.sdui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject
import ru.rcfh.core.sdui.common.IndexAware

class TablePageState(
    val origin: ImmutableList<TextState>,
    val actual: ImmutableList<TextState>,
    useActual: Boolean
) : IndexAware {
    private var rowIndex by mutableIntStateOf(-1)

    private var _useActual by mutableStateOf(useActual)
    var useActual: Boolean
        get() = _useActual
        set(value) {
            origin.forEach {
                it.enabled = !useActual
            }
            actual.forEach {
                it.enabled = useActual
            }
            _useActual = value
        }

    operator fun component1() = origin
    operator fun component2() = actual

    override val mIndex: Int
        get() = rowIndex

    fun getActive(): List<TextState> {
        return if (useActual) actual else origin
    }

    override fun updateIndex(index: Int) {
        rowIndex = index
        origin.forEach { it.updateIndex(index) }
        actual.forEach { it.updateIndex(index) }
    }

    operator fun get(templateId: String): TextState? {
        return if (useActual) {
            actual.find { it.id == templateId }
        } else {
            origin.find { it.id == templateId }
        }
    }

    fun save(): JsonElement {
        return buildJsonObject {
            putJsonObject("origin") {
                origin.forEach { state ->
                    put(state.id, state.save())
                }
            }
            putJsonObject("actual") {
                actual.forEach { state ->
                    put(state.id, state.save())
                }
            }
            put("useActual", JsonPrimitive(useActual))
        }
    }
}