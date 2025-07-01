package ru.rcfh.core.sdui.data

import ru.rcfh.core.sdui.common.Rule
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.template.FormTemplate
import ru.rcfh.core.sdui.template.Template

val Form3 = FormTemplate(
    id = 3,
    name = "III. Форма ввода дополнительных сведений лесоустройства",
    templates = listOf(
        Template.Text(
            id = "sostavpod",
            label = "Подрост. Состав",
            visual = Visual.Text()
        ),
        Template.Text(
            id = "vozrpod",
            label = "Подрост. Средний возраст",
            rules = listOf(
                Rule.Required("Поле не заполнено"),
                Rule.DigitFormat(
                    decimalSize = 1,
                    message = DIGIT_MESSAGE
                )
            ),
            visual = Visual.Decimal(
                unit = "лет"
            )
        ),
        Template.Text(
            id = "hpod",
            label = "Подрост. Средняя высота",
            rules = listOf(
                Rule.DigitFormat(
                    decimalSize = 2,
                    precise = 1,
                    message = DIGIT_MESSAGE
                )
            ),
            visual = Visual.Number(
                unit = "м"
            )
        ),
        Template.Text(
            id = "gustpod",
            label = "Подрост. Густота",
            rules = listOf(
                Rule.DigitFormat(
                    decimalSize = 3,
                    precise = 2,
                    message = DIGIT_MESSAGE
                )
            ),
            visual = Visual.Number(
                unit = "тыс. шт./ га"
            )
        ),
        Template.Text(
            id = "sostpod",
            label = "Подрост. Состояние",
            visual = Visual.Reference(handbookId = 23)
        ),
        Template.Text(
            id = "sostavpdl",
            label = "Подлесок. Породы",
            visual = Visual.Text()
        ),
        Template.Text(
            id = "gustpdl",
            label = "Подлесок. Густота",
            visual = Visual.Reference(handbookId = 1) // TODO Справочник густоты подлеска
        ),
        Template.Text(
            id = "hpdl",
            label = "Подлесок. Средняя высота",
            rules = listOf(
                Rule.DigitFormat(
                    decimalSize = 2,
                    precise = 1,
                    message = DIGIT_MESSAGE
                )
            ),
            visual = Visual.Number(
                unit = "м"
            )
        ),
        Template.Text(
            id = "sostpdl",
            label = "Подлесок. Состояние",
            visual = Visual.Reference(handbookId = 1) // TODO Справочник состояния подлеска
        ),
        Template.Text(
            id = "reljeff",
            label = "Рельеф. Форма",
            visual = Visual.Reference(handbookId = 21)
        ),
        Template.Text(
            id = "reljefeksp",
            label = "Рельеф. Экспозиция",
            visual = Visual.Reference(handbookId = 17)
        ),
        Template.Text(
            id = "reljefuklon",
            label = "Рельеф. Уклон",
            rules = listOf(
                Rule.DigitFormat(
                    decimalSize = 2,
                    message = DIGIT_MESSAGE
                )
            ),
            visual = Visual.Decimal(unit = "°")
        ),
        Template.Text(
            id = "eroziavid",
            label = "Эрозия. Вид",
            visual = Visual.Reference(handbookId = 28)
        ),
        Template.Text(
            id = "eroziastep",
            label = "Эрозия. Степень",
            visual = Visual.Reference(handbookId = 22)
        ),
        Template.Text(
            id = "provlhm",
            label = "Проведенные хозяйственные мероприятия",
            visual = Visual.Reference(handbookId = 1) // TODO Справочник лесохозяйственных мероприятий
        ),
        Template.Text(
            id = "itaks3",
            label = "Информация пользователя",
            visual = Visual.Text(multiline = true)
        ),
    )
)