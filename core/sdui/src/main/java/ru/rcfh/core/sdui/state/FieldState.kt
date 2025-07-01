package ru.rcfh.core.sdui.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.JsonElement
import ru.rcfh.core.sdui.common.DetectedError

sealed class FieldState(val document: DocumentState) {
    abstract val id: String

    abstract fun isValid(): Boolean

    abstract fun save(): JsonElement

    abstract fun detectErrors(): List<DetectedError>

    val documentScope: CoroutineScope
        get() = document.documentScope
}