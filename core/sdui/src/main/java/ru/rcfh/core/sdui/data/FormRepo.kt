package ru.rcfh.core.sdui.data

import ru.rcfh.core.sdui.template.DocumentTemplate
import ru.rcfh.core.sdui.template.FormOptions
import ru.rcfh.core.sdui.template.FormTab
import ru.rcfh.core.sdui.template.FormTemplate
import ru.rcfh.datastore.SettingsRepository

class FormRepo(
    private val settingsRepository: SettingsRepository
) {
    suspend fun getDocumentTemplate(): DocumentTemplate {
        return DocumentTemplate(
            forms = listOf(
                FormTab(formId = 1, name = "I"),
                FormTab(formId = 2, name = "II"),
                FormTab(formId = 3, name = "III"),
                FormOptions(
                    optionId = "option4or5",
                    tabs = listOf(
                        FormTab(formId = 4, name = "IV"),
                        FormTab(formId = 5, name = "V")
                    )
                ),
                FormTab(formId = 6, name = "VI")
            ),
        )
    }

    fun getForm(id: Int): FormTemplate? {
        return getForms().find { it.id == id }
    }

    fun getForms(): List<FormTemplate> {
        return listOf(
            Form1,
            Form2,
            Form3,
            Form4,
            Form5,
            Form6
        )
    }

    fun getFormIds(): List<Int> = getFormList().map { it.formId }

    fun getFormList(): List<FormTab> {
        return getForms().map {
            FormTab(
                formId = it.id,
                name = it.name
            )
        }
    }

}