package ru.rcfh.core.sdui.data

import ru.rcfh.core.sdui.common.Rule
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.common.toFormula
import ru.rcfh.core.sdui.template.FormTemplate
import ru.rcfh.core.sdui.template.Template

val Form4 = FormTemplate(
    id = 4,
    name = "IV. Форма ввода ВНН за санитарным состоянием насаждений глазомерным методом",
    templates = listOf(
        Template.Table(
            id = "4table",
            name = "Таблица 4 формы",
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
                Template.Linked(
                    id = "vozr",
                    label = "Ср. возраст"
                ),
                Template.Linked(
                    id = "hsr",
                    label = "Ср. высота"
                ),
                Template.Linked(
                    id = "dsr",
                    label = "Ср. диаметр"
                ),
                Template.Linked(
                    id = "zapaspor",
                    label = "Запас"
                ),
                Template.Ratio(
                    id = "4ratio",
                    templates = listOf(
                        Template.Text(
                            id = "bpodolap",
                            label = "I\nздор",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "osldolap",
                            label = "II\nослаб",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "sosldolap",
                            label = "III\nс.осл.",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "usdolap",
                            label = "IV\nусых",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "svsuhdolap",
                            label = "V(А)\nсв.сc.",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "svvtrdolap",
                            label = "V(Б)\nсв.вв.",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "svburdolap",
                            label = "V(В)\nсв.бл.",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "stsuhdolap",
                            label = "V(Г)\nст.сс.",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "stvtrdolap",
                            label = "V(Д)\nст.вв.",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "stburdolap",
                            label = "V(E)\nст.бл.",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                    )
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
                Template.Repeatable(
                    id = "4repeatable",
                    name = "Признаки повреждения по породам",
                    templates = listOf(
                        Template.Text(
                            id = "prichina",
                            label = "Причины повреждения",
                            visual = Visual.Reference(handbookId = 10),
                            rules = listOf(Rule.Required("Поле не заполнено"))
                        ),
                        Template.Text(
                            id = "godpovr",
                            label = "Год ослабления, повреждения насаждения",
                            visual = Visual.Date(format = "yyyy"),
                            rules = listOf(Rule.Required("Поле не заполнено"))
                        ),
                        Template.Repeatable(
                            id = "priznList",
                            maxEntries = 5,
                            name = "Причина",
                            templates = listOf(
                                Template.Text(
                                    id = "prizn",
                                    label = "Признак",
                                    visual = Visual.Reference(handbookId = 9, dependsOn = "prichina"),
                                    rules = listOf(Rule.Required("Поле не заполнено"))
                                ),
                                Template.Text(
                                    id = "dolaprizn",
                                    label = "Доля повреждённых деревьев от запаса породы",
                                    visual = Visual.Decimal(unit = "%"),
                                    rules = listOf(
                                        Rule.Required("Поле не заполнено"),
                                        Rule.DigitFormat(
                                            decimalSize = 3,
                                            message = DIGIT_MESSAGE
                                        )
                                    )
                                ),
                            )
                        )
                    )
                )
            )
        ),
        Template.Text(
            id = "charochag",
            label = "Характер усыхания насаждения",
            visual = Visual.Reference(handbookId = 20)
        ),
        Template.Text(
            id = "itaks4",
            label = "Информация пользователя по разделу",
            visual = Visual.Text(multiline = true)
        )
    )
)