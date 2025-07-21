package ru.rcfh.core.sdui.common

sealed interface Format {
    data object CadastralNumber : Format
    data class FullnessLimits(val fieldId: String, val errorMsg: String) : Format
    data object None : Format
}