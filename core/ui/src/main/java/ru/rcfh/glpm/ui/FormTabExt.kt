package ru.rcfh.glpm.ui

import android.content.Context
import ru.rcfh.core.model.FormTab

fun FormTab.Tab.getDisplayName(context: Context): String {
    return when (formId) {
        1 -> context.getString(R.string.core_ui_formTabName0)
        2 -> context.getString(R.string.core_ui_formTabName1)
        3 -> context.getString(R.string.core_ui_formTabName2)
        4 -> context.getString(R.string.core_ui_formTabName3)
        5 -> context.getString(R.string.core_ui_formTabName4)
        6 -> context.getString(R.string.core_ui_formTabName5)
        else -> error("Resource for tab with $formId not found")
    }
}

fun FormTab.Tab.getRomanNumber(context: Context): String {
    return when (formId) {
        1 -> context.getString(R.string.core_ui_formTabShortName0)
        2 -> context.getString(R.string.core_ui_formTabShortName1)
        3 -> context.getString(R.string.core_ui_formTabShortName2)
        4 -> context.getString(R.string.core_ui_formTabShortName3)
        5 -> context.getString(R.string.core_ui_formTabShortName4)
        6 -> context.getString(R.string.core_ui_formTabShortName5)
        else -> error("Resource for tab with $formId not found")
    }
}

fun FormTab.Tab.getFullName(context: Context): String {
    return "${getRomanNumber(context)}. ${getDisplayName(context)}"
}