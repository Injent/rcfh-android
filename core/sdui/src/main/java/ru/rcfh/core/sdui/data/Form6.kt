package ru.rcfh.core.sdui.data

import ru.rcfh.core.sdui.common.Rule
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.common.toFormula
import ru.rcfh.core.sdui.template.FormTemplate
import ru.rcfh.core.sdui.template.Template

val Form6 = FormTemplate(
    id = 6,
    name = "VI. Сводка по насаждению и рекомендуемые мероприятия",
    templates = listOf(
        Template.Table(
            id = "6table",
            name = "Сводка по состоянию насаждения",
            dependency = "species_specs",
            columns = listOf(
                Template.Linked(
                    id = "yarus",
                    label = "Ярус"
                ),
                Template.Linked(
                    id = "dolap",
                    label = "Доля"
                ),
                Template.Linked(
                    id = "kodporoda",
                    label = "Порода"
                ),
                Template.Calculated(
                    id = "sksporoda",
                    label = "СКС",
                    formula = "({bpodolap}+{osldolap}*2+{sosldolap}*3+{usdolap}*4+({svsuhdolap}+{svvtrdolap}+{svburdolap}+{stsuhdolap}+{stvtrdolap}+{stburdolap})*5)/100".toFormula()
                ),
                Template.Calculated(
                    id = "ooporoda",
                    label = "Отпад\nОбщ.",
                    formula = "{usdolap}+{svsuhdolap}+{svvtrdolap}+{svburdolap}+{stsuhdolap}+{stvtrdolap}+{stburdolap}".toFormula(),
                    unit = "%"
                ),
                Template.Calculated(
                    id = "toporoda",
                    label = "Отпад\nТек.",
                    formula = "{usdolap}+{svsuhdolap}+{svvtrdolap}+{svburdolap}".toFormula(),
                    unit = "%"
                ),
            )
        ),
        Template.Repeatable(
            id = "6repeatable",
            name = "Очаг вредного организма",
            templates = listOf(
                Template.Text(
                    id = "ochagvidvo",
                    label = "Вид ВО",
                    visual = Visual.Reference(handbookId = 8),
                    rules = listOf(
                        Rule.Required("Поле не заполнено")
                    )
                ),
                Template.Text(
                    id = "ochagstep",
                    label = "Степень повреждения",
                    visual = Visual.Reference(handbookId = 1), // TODO
                    rules = listOf(
                        Rule.Required("Поле не заполнено")
                    )
                ),
                Template.Text(
                    id = "primechochag",
                    label = "Примечание к очагу",
                    visual = Visual.Text(multiline = true),
                    rules = listOf(
                        Rule.Required("Поле не заполнено")
                    )
                )
            )
        ),
        Template.Repeatable(
            id = "6repeatable1",
            name = "Рекомендуемые мероприятия",
            templates = listOf(
                Template.Text(
                    id = "rekvidmer",
                    label = "Вид",
                    visual = Visual.Reference(handbookId = 25),
                    rules = listOf(
                        Rule.Required("Поле не заполнено")
                    )
                ),
                Template.Text(
                    id = "srokmzl",
                    label = "Сроки проведения",
                    visual = Visual.Text()
                ),
                Template.Text(
                    id = "srekmer",
                    label = "Площадь",
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 4,
                            precise = 4,
                            message = DIGIT_MESSAGE
                        )
                    ),
                    visual = Visual.Number(unit = "га")
                ),
                Template.Text(
                    id = "primechmzl1",
                    label = "Примечание к меропр.",
                    visual = Visual.Text()
                )
            )
        ),
        Template.Text(
            id = "itaks6",
            label = "Информация пользователя",
            visual = Visual.Text(multiline = true)
        )
    )
)
