package ru.rcfh.core.sdui.common

data class ErrorReport(
    val severe: List<DetectedError> = emptyList(),
    val warnings: List<DetectedError> = emptyList()
) {
    val hasErrors: Boolean
        get() = severe.isNotEmpty() || warnings.isNotEmpty()
}