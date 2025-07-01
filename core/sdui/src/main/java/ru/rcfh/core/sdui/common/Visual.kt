package ru.rcfh.core.sdui.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Visual {
    @Serializable
    @SerialName("text")
    data class Text(
        val multiline: Boolean = false
    ) : Visual

    @Serializable
    @SerialName("decimal")
    data class Decimal(val unit: String? = null) : Visual

    @Serializable
    @SerialName("number")
    data class Number(val unit: String? = null) : Visual

    @Serializable
    @SerialName("date")
    data class Date(
        val format: String = "dd.MM.yyyy",
        val autofill: Boolean = false,
        val changeOnEdit: Boolean = false
    ) : Visual

    @Serializable
    @SerialName("checkbox")
    data object Checkbox : Visual

    @Serializable
    @SerialName("reference")
    data class Reference(val handbookId: Int) : Visual
}