package ru.rcfh.core.sdui.data

import ru.rcfh.core.sdui.common.Rule
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.template.FormTemplate
import ru.rcfh.core.sdui.template.Template

val Form2 = FormTemplate(
    id = 2,
    name = "II. Форма ввода таксационной информации",
    templates = listOf(
        Template.Text(
            id = "cnl",
            label = "Целевое назначение лесов",
            rules = listOf(Rule.Required("Поле пусто")),
            visual = Visual.Reference(handbookId = 16)
        ),
        Template.Text(
            id = "kzl",
            label = "Кат. ЗЛ",
            rules = listOf(Rule.Required("Поле пусто")),
            visual = Visual.Reference(handbookId = 5)
        ),
        Template.Text(
            id = "proishozdenie",
            label = "Происхождение насаждения",
            visual = Visual.Reference(handbookId = 24)
        ),
        Template.Text(
            id = "ozu",
            label = "ОЗУ",
            visual = Visual.Reference(handbookId = 1)
        ),
        Template.Text(
            id = "nozu",
            label = "Иное ОЗУ",
            visual = Visual.Text()
        ),
        Template.Text(
            id = "arenda",
            label = "Право пользования",
            visual = Visual.Reference(handbookId = 11)
        ),
        Template.Text(
            id = "vidarenda",
            label = "Вид использования",
            visual = Visual.Reference(handbookId = 4)
        ),
        Template.Text(
            id = "oopt",
            label = "ООПТ",
            visual = Visual.Reference(handbookId = 3)
        ),
        Template.Text(
            id = "noopt",
            label = "Иное ООПТ",
            visual = Visual.Text()
        ),
        Template.Text(
            id = "sootvlu",
            label = "Соответствие таксационным описаниям",
            rules = listOf(Rule.Required("Поле пусто")),
            visual = Visual.Checkbox
        ),
        Template.Text(
            id = "glu",
            label = "Год лесоустройства",
            rules = listOf(Rule.Required("Поле пусто")),
            visual = Visual.Date(format = "yyyy")
        ),
        Template.ComparisonTable(
            id = "2table",
            name = "Характеристика насаждения общая",
            maxEntries = 1,
            templates = listOf(
                Template.Text(
                    id = "katzemf",
                    label = "Кат. земель",
                    rules = listOf(Rule.Required("Поле пусто")),
                    visual = Visual.Reference(handbookId = 6)
                ),
                Template.Text(
                    id = "gporodato",
                    label = "Главная порода",
                    rules = listOf(Rule.Required("Поле пусто")),
                    visual = Visual.Reference(handbookId = 31)
                ),
                Template.Text(
                    id = "bonitetf",
                    label = "Бонитет",
                    rules = listOf(
                        Rule.Required("Поле пусто")
                    ),
                    visual = Visual.Reference(handbookId = 7)
                ),
                Template.Text(
                    id = "tiplesaf",
                    label = "Тип леса",
                    visual = Visual.Text()
                ),
                Template.Text(
                    id = "tluf",
                    label = "ТЛУ",
                    visual = Visual.Reference(handbookId = 13)
                ),
                Template.Text(
                    id = "zapassuhf",
                    label = "Запас сухостоя, кбм на выдел",
                    visual = Visual.Number(unit = "м3"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 6,
                            precise = 2,
                            message = DIGIT_MESSAGE
                        )
                    )
                ),
                Template.Text(
                    id = "zapassuhf",
                    label = "Запас редин, кбм на выдел",
                    visual = Visual.Number(unit = "м3"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 6,
                            precise = 2,
                            message = DIGIT_MESSAGE
                        )
                    )
                ),
                Template.Text(
                    id = "zapasedf",
                    label = "Запас един. дер., кбм на выдел",
                    visual = Visual.Number(unit = "м3"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 6,
                            precise = 2,
                            message = DIGIT_MESSAGE
                        )
                    )
                ),
                Template.Text(
                    id = "zapaszahlf",
                    label = "Запас захл. общ., кбм на выдел",
                    visual = Visual.Number(unit = "м3"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 6,
                            precise = 2,
                            message = DIGIT_MESSAGE
                        )
                    )
                ),
                Template.Text(
                    id = "zapaszahllikf",
                    label = "Запас захл. ликвид, кбм на выдел",
                    visual = Visual.Number(unit = "м3"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 6,
                            precise = 2,
                            message = DIGIT_MESSAGE
                        )
                    )
                ),
            )
        ),
        Template.ComparisonTable(
            id = "2table2",
            name = "Характеристика насаждения по ярусам",
            templates = listOf(
                Template.Text(
                    id = "yarushm",
                    label = "Ярус",
                    rules = listOf(
                        Rule.Required("Поле пусто"),
                        Rule.DigitFormat(
                            decimalSize = 1,
                            message = DIGIT_MESSAGE
                        )
                    ),
                    visual = Visual.Decimal(unit = null)
                ),
                Template.Text(
                    id = "sostavto",
                    label = "Состав насаждения",
                    rules = listOf(Rule.Required("Поле пусто")),
                    visual = Visual.Text()
                ),
                Template.Text(
                    id = "visotato",
                    label = "Высота яруса",
                    visual = Visual.Decimal(unit = "м"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 3,
                            message = DIGIT_MESSAGE
                        )
                    )
                ),
                Template.Text(
                    id = "klvozrto",
                    label = "Класс возраста",
                    visual = Visual.Reference(handbookId = 26),
                ),
                Template.Text(
                    id = "grvozrto",
                    label = "Группа возраста",
                    visual = Visual.Reference(handbookId = 27),
                ),
                Template.Text(
                    id = "polnota",
                    label = "Полнота",
                    rules = listOf(
                        Rule.Required("Поле пусто"),
                        Rule.DigitFormat(
                            decimalSize = 1,
                            precise = 2,
                            message = DIGIT_MESSAGE
                        ),
                        Rule.Range(
                            min = 0f,
                            max = 1.3f,
                            message = "Число превышает максимальное значение (1,3)"
                        )
                    ),
                    visual = Visual.Number(unit = "0,1 доли ед.")
                ),
                Template.Text(
                    id = "zapassirgato",
                    label = "Запас сырораст., кбм на га",
                    rules = listOf(
                        Rule.Required("Поле пусто"),
                        Rule.DigitFormat(
                            decimalSize = 6,
                            precise = 2,
                            message = DIGIT_MESSAGE
                        )
                    ),
                    visual = Visual.Number(unit = "м3")
                ),
                Template.Text(
                    id = "zapassirto",
                    label = "Запас сырораст., кбм на выдел",
                    rules = listOf(
                        Rule.Required("Поле пусто"),
                        Rule.DigitFormat(
                            decimalSize = 6,
                            precise = 2,
                            message = DIGIT_MESSAGE
                        )
                    ),
                    visual = Visual.Number(unit = "м3")
                )
            )
        ),
        Template.ComparisonTable(
            id = "species_specs",
            name = "Характеристика породы",
            templates = listOf(
                Template.Text(
                    id = "yarus",
                    label = "Ярус",
                    rules = listOf(
                        Rule.Required("Поле пусто"),
                        Rule.DigitFormat(
                            decimalSize = 1,
                            message = DIGIT_MESSAGE
                        )
                    ),
                    visual = Visual.Decimal(unit = null),
                ),
                Template.Text(
                    id = "dolap",
                    label = "Доля",
                    rules = listOf(
                        Rule.Required("Поле пусто"),
                        Rule.DigitFormat(
                            decimalSize = 2,
                            message = DIGIT_MESSAGE
                        )
                    ),
                    visual = Visual.Decimal(unit = null, canSetPlus = true),
                ),
                Template.Text(
                    id = "kodporoda",
                    label = "Порода",
                    rules = listOf(Rule.Required("Поле пусто")),
                    visual = Visual.Reference(handbookId = 31),
                ),
                Template.Text(
                    id = "vozr",
                    label = "Ср. возраст",
                    visual = Visual.Decimal(unit = "лет"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 3,
                            message = DIGIT_MESSAGE
                        )
                    )
                ),
                Template.Text(
                    id = "hsr",
                    label = "Ср. высота",
                    visual = Visual.Number(unit = "м"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 2,
                            precise = 1,
                            message = DIGIT_MESSAGE
                        )
                    )
                ),
                Template.Text(
                    id = "dsr",
                    label = "Ср. диаметр",
                    visual = Visual.Number(unit = "см"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 3,
                            precise = 1,
                            message = DIGIT_MESSAGE
                        )
                    )
                ),
                Template.Text(
                    id = "zapaspor",
                    label = "Запас, кбм на га",
                    visual = Visual.Number(unit = "м3"),
                    rules = listOf(
                        Rule.DigitFormat(
                            decimalSize = 6,
                            precise = 2,
                            message = DIGIT_MESSAGE
                        )
                    )
                )
            )
        ),
        Template.Text(
            id = "itaks2",
            label = "Информация пользователя",
            visual = Visual.Text(multiline = true)
        )
    )
)