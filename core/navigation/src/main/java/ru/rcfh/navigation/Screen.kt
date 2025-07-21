package ru.rcfh.navigation

import androidx.navigation.NavDestination
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Loading : Screen

    @Serializable
    data class FormNavigator(
        val documentId: Int,
        val formId: Int
    ) : Screen

    @Serializable
    data class Form(
        val documentId: Int,
        val formId: Int,
        val optionId: String?
    ) : Screen

    @Serializable
    data class TableRecord(
        val documentId: Int,
        val formId: Int,
        val templateId: String,
        val rowIdx: Int
    ) : Screen

    @Serializable
    data class Table(
        val documentId: Int,
        val formId: Int,
        val templateId: String,
    ) : Screen

    @Serializable
    data class ComparisonTable(
        val documentId: Int,
        val formId: Int,
        val templateId: String,
        val initialRowIdx: Int? = null
    ) : Screen

    @Serializable
    data class Documents(val documentId: Int? = null) : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object SignIn : Screen

    @Serializable
    data class Summarize(val documentId: Int) : Screen

    @Serializable
    data object SignUp : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    class HandbookSearch(
        val documentId: Int,
        val handbookId: Int,
        val callbackId: String,
        val title: String,
        val selectedOption: String?,
        val rowIndex: Int,
        val templateId: String,
        val dependencyHandbook: Int?,
        val dependencyRefId: Int?,
        val shouldHaveFilledDependency: Boolean
    ) : Screen

    @Serializable
    data object Guide : Screen
}

fun NavDestination.toRoute(): String? {
    return route?.substringBefore('?')?.substringBefore('/')
}

inline fun <reified T : Screen> getRoute(): String {
    return T::class.qualifiedName ?: "Unknown"
}