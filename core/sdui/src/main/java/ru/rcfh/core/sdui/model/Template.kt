package ru.rcfh.core.sdui.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Template {
    val id: String

    @Serializable
    @SerialName("text")
    data class Text(
        override val id: String,
        val name: String,
        val required: Boolean,
        val kind: Kind,
        val hint: String? = null,
        val regex: String? = null,
        val errorMsg: String? = null,
        val placeholder: String = run {
            when (kind) {
                is Kind.Date -> kind.format
                    .replace("d", "Д")
                    .replace("y", "Г")
                is Kind.Float -> buildString {
                    if (kind.maxIntDigits == null || kind.maxFractionDigits == null) {
                        append("0.0")
                        return@buildString
                    }
                    append("0".repeat(kind.maxIntDigits))
                    append(".")
                    append("0".repeat(kind.maxFractionDigits))
                }
                is Kind.Int -> "0".repeat(kind.maxDigits ?: 0)
                else -> ""
            }
        }
    ) : Template

    @Serializable
    @SerialName("table")
    data class Table(
        override val id: String,
        val name: String,
        val columns: List<Template>,
    ) : Template

    @Serializable
    @SerialName("comparison_table")
    data class ComparisonTable(
        override val id: String,
        val name: String,
        val repeatable: Boolean,
        val section: List<Section>
    ) : Template {
        @Serializable
        data class Section(
            val name: String,
            val templates: List<Text>
        )
    }

    @Serializable
    @SerialName("repeatable")
    data class Repeatable(
        override val id: String,
        val name: String,
        val templates: List<Text>
    ) : Template
}