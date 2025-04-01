package ru.rcfh.core.sdui.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Kind {
    @Serializable
    @SerialName("string")
    data class String(
        val maxLines: kotlin.Int = 1
    ) : Kind

    @Serializable
    @SerialName("int")
    data class Int(
        val unit: kotlin.String? = null,
        val maxDigits: kotlin.Int? = null
    ) : Kind

    @Serializable
    @SerialName("float")
    data class Float(
        val unit: kotlin.String? = null,
        val maxIntDigits: kotlin.Int? = null,
        val maxFractionDigits: kotlin.Int? = null
    ) : Kind

    @Serializable
    @SerialName("date")
    data class Date(
        val format: kotlin.String = "dd.MM.yyyy"
    ) : Kind

    @Serializable
    @SerialName("reference")
    data class Reference(
        val handbookId: kotlin.Int
    ) : Kind

    @Serializable
    @SerialName("bool")
    data object Bool : Kind
}