package ru.rcfh.core.sdui.common

data class DetectedError(
    val templateId: String,
    val name: String,
    val error: String,
    val type: ErrorType,
    val address: ErrorAddress? = null
)

enum class ErrorType {
    SEVERE,
    WARNING
}