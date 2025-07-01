package ru.rcfh.core.sdui.template

import kotlinx.serialization.Serializable

@Serializable
data class DocumentTemplate(
    val forms: List<FormElement>,
)
