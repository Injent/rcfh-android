package ru.rcfh.core.sdui.state

import kotlinx.collections.immutable.ImmutableList

class FormState(
    val id: Int,
    val name: String,
    val fields: ImmutableList<FieldState>
) {
    operator fun get(templateId: String): FieldState? {
        return fields.find { it.id == templateId }
    }

    fun isValid(): Boolean = fields.all(FieldState::isValid)
}