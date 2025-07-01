package ru.rcfh.core.sdui.template

sealed interface FormElement

data class FormTab(
    val formId: Int,
    val name: String,
) : FormElement

data class FormOptions(
    val optionId: String,
    val tabs: List<FormTab>
) : FormElement