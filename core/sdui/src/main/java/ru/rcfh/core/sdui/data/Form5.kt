package ru.rcfh.core.sdui.data

import ru.rcfh.core.sdui.common.Rule
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.common.toFormula
import ru.rcfh.core.sdui.template.FormTemplate
import ru.rcfh.core.sdui.template.Template

val Form5 = FormTemplate(
    id = 5,
    name = "V. Форма ввода ВНН за лесопатологическим состоянием насаждений глазомерным методом",
    templates = listOf(
        Template.Repeatable(
            id = "prichiniPovrezhdeniya2",
            name = "Причины повреждения",
            templates = listOf(
                Template.Text(
                    id = "prichina2",
                    label = "Причины повреждения",
                    visual = Visual.Reference(handbookId = 10), // TODO
                    rules = listOf(
                        Rule.Required("Поле не заполнено")
                    )
                ),
                Template.Text(
                    id = "godpovr2",
                    label = "Год ослабления, повреждения насаждения",
                    visual = Visual.Date(format = "yyyy"),
                    rules = listOf(
                        Rule.Required("Поле не заполнено")
                    )
                )
            )
        ),
        Template.Table(
            id = "table5",
            name = "Таблица",
            dependency = "species_specs",
            total = mapOf(
                "objedola0" to Template.Calculated(
                    id = "objedolan0",
                    label = "",
                    formula = "SUM({objedola0}*{dolap})/10".toFormula(),
                ),
                "objedola25" to Template.Calculated(
                    id = "objedolan25",
                    label = "",
                    formula = "SUM({objedola25}*{dolap})/10".toFormula()
                ),
                "objedola50" to Template.Calculated(
                    id = "objedolan50",
                    label = "",
                    formula = "SUM({objedola50}*{dolap})/10".toFormula(),
                ),
                "objedola75" to Template.Calculated(
                    id = "objedolan75",
                    label = "",
                    formula = "SUM({objedola75}*{dolap})/10".toFormula()
                ),
                "objedola100" to Template.Calculated(
                    id = "objedolan100",
                    label = "",
                    formula = "SUM({objedola100}*{dolap})/10".toFormula()
                ),
                "dolapovr" to Template.Calculated(
                    id = "dolapovrn",
                    label = "",
                    formula = "SUM({dolapovr}*{dolap})/10".toFormula()
                )
            ),
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
                Template.Ratio(
                    id = "5ratio",
                    templates = listOf(
                        Template.Text(
                            id = "objedola0",
                            label = "0",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "objedola25",
                            label = "1 - 25%",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "objedola50",
                            label = "26 – 50%",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "objedola75",
                            label = "51 - 75%",
                            rules = listOf(
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            ),
                            visual = Visual.Decimal(unit = "%")
                        ),
                        Template.Text(
                            id = "objedola100",
                            label = "75.1 - 100%",
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
                    id = "dolapovr",
                    label = "Доля поврежденных деревьев, % от количества",
                    formula = "{objedola25}+{objedola50}+{objedola75}+{objedola100}".toFormula()
                ),
                Template.Repeatable(
                    id = "5repeatable2",
                    name = "Признаки повреждения по породам",
                    templates = listOf(
                        Template.Text(
                            id = "prizn2",
                            label = "Признак",
                            visual = Visual.Reference(handbookId = 10), // TODO
                            rules = listOf(Rule.Required("Поле не заполнено"))
                        ),
                        Template.Text(
                            id = "dolaprizn2",
                            label = "Доля повреждённых деревьев от запаса породы",
                            visual = Visual.Decimal(unit = "%"),
                            rules = listOf(
                                Rule.Required("Поле не заполнено"),
                                Rule.DigitFormat(
                                    decimalSize = 3,
                                    message = DIGIT_MESSAGE
                                )
                            )
                        )
                    )
                )
            )
        ),
        Template.Text(
            id = "itaks5",
            label = "Информация пользователя",
            visual = Visual.Text(multiline = true)
        )
    )
)
