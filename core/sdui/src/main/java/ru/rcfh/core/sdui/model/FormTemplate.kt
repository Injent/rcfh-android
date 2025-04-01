package ru.rcfh.core.sdui.model

import kotlinx.serialization.Serializable

@Serializable
data class FormTemplate(
    val id: Int,
    val name: String,
    val templates: List<Template>,
    val groups: List<Group>
)
