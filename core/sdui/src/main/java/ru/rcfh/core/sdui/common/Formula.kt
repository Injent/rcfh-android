package ru.rcfh.core.sdui.common

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Formula(
    val value: String
) {
    fun isProductFormula() = value.contains("SUM", ignoreCase = true)
}

fun String.toFormula() = Formula(this)