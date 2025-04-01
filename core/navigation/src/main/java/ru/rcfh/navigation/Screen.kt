package ru.rcfh.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data class Forms(val formId: Int) : Screen

    @Serializable
    data class RowEditor(
        val draftId: String,
        val templateId: String,
        val index: Int
    ) : Screen

    @Serializable
    data class SectionEditor(
        val draftId: String,
        val templateId: String
    ) : Screen

    @Serializable
    data object Templates : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data class HandbookDialog(
        val handbookId: Int,
        val draftId: String,
        val templateId: String,
        val parentTemplateId: String?,
        val row: Int = 0,
        val iteration: Int = 0
    ) : Screen
}