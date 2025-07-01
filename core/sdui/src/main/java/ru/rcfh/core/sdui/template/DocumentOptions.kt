package ru.rcfh.core.sdui.template

import kotlinx.serialization.Serializable

@Serializable
data class DocumentOptions(
    val formOptions: Map<String, Int> = emptyMap()
)