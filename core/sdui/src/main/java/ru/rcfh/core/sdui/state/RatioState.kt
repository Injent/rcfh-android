package ru.rcfh.core.sdui.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import ru.rcfh.core.sdui.common.DetectedError

@Stable
class RatioState(
    override val id: String,
    val values: ImmutableList<TextState>,
    documentState: DocumentState,
) : FieldState(documentState), Container {
    val maxLimitReached by derivedStateOf {
        values[0].value.toIntOrNull() == 0
    }
    val minLimitReached by derivedStateOf {
        values[0].value.toIntOrNull() == 100
    }

    override fun isValid() = values.all { it.isValid() }

    override fun items(): List<FieldState> {
        return values
    }

    override fun detectErrors(): List<DetectedError> {
        return values.mapNotNull {
            it.detectErrors().singleOrNull()
        }
    }

    override fun save(): JsonElement {
        return buildJsonObject {
            values.forEach { state ->
                put(state.id, state.save())
            }
        }
    }
}