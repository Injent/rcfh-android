package ru.rcfh.core.sdui.data

import ru.rcfh.core.sdui.common.Format
import ru.rcfh.core.sdui.common.Rule
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.template.FormTemplate
import ru.rcfh.core.sdui.template.Template

const val DIGIT_MESSAGE = "Размер значения не допускается"

val Form1 = FormTemplate(
    id = 1,
    name = "I. Форма ввода данных «Лесохозяйственного» адреса участка",
    templates = listOf(
        Template.Location(
            id = "location",
            latitude = Template.Text(
                id = "lat",
                label = "Широта",
                visual = Visual.Number(),
                rules = listOf(
                    Rule.Required("Поле пусто"),
                    Rule.DigitFormat(
                        decimalSize = 3,
                        precise = 6,
                        message = DIGIT_MESSAGE
                    )
                )
            ),
            longitude = Template.Text(
                id = "lon",
                label = "Долгота",
                visual = Visual.Number(),
                rules = listOf(
                    Rule.Required("Поле пусто"),
                    Rule.DigitFormat(
                        decimalSize = 3,
                        precise = 6,
                        message = DIGIT_MESSAGE
                    )
                )
            ),
        ),
        Template.Text(
            id = "vnum",
            label = "ВНУМ",
            visual = Visual.Number(unit = "м"),
            rules = listOf(
                Rule.DigitFormat(
                    decimalSize = 5,
                    message = DIGIT_MESSAGE
                )
            )
        ),
        Template.Text(
            id = "filial",
            label = "Организация, проводившая работу",
            visual = Visual.Reference(handbookId = 15),
            rules = listOf(
                Rule.Required("Поле пусто")
            ),
        ),
        Template.Text(
            id = "srf",
            label = "Субъект Российской Федерации",
            visual = Visual.Reference(handbookId = 12),
            rules = listOf(
                Rule.Required("Поле пусто")
            ),
        ),
        Template.Text(
            id = "lvo",
            label = "Лесничество",
            visual = Visual.Reference(handbookId = 36),
            rules = listOf(
                Rule.Required("Лесничество не выбрано")
            )
        ),
        Template.Text(
            id = "ulvo",
            label = "Уч. лесничество",
            visual = Visual.Reference(handbookId = 36),
            rules = listOf(
                Rule.Required("Участок не выбран")
            )
        ),
        Template.Text(
            id = "urc",
            label = "Урочище (лесная дача и т.п.)",
            visual = Visual.Reference(handbookId = 36),
            rules = listOf(
                Rule.Required("Урочище не выбрано")
            )
        ),
        Template.Text(
            id = "kv",
            label = "Квартал",
            visual = Visual.Text(),
            rules = listOf(
                Rule.Required("Квартал не указан")
            )
        ),
        Template.Text(
            id = "vid",
            label = "Выдел",
            visual = Visual.Text(),
            rules = listOf(
                Rule.Required("Выдел не указан")
            )
        ),
        Template.Text(
            id = "s",
            label = "Площадь лесотаксационного выдела",
            visual = Visual.Number(unit = "га"),
            rules = listOf(
                Rule.Required("Площадь не указана"),
                Rule.DigitFormat(
                    decimalSize = 4,
                    precise = 4,
                    message = DIGIT_MESSAGE
                )
            )
        ),
        Template.Text(
            id = "lpvid",
            label = "Номер лесопатологического выдела",
            visual = Visual.Text()
        ),
        Template.Text(
            id = "slp",
            label = "Площадь лесотаксационного выдела или лесопатологического выдела (при его выделении)",
            format = Format.FullnessLimits(fieldId = "s", errorMsg = "Превышает площадь лесотаксационного выдела"),
            visual = Visual.Number(unit = "га"),
            rules = listOf(
                Rule.Required("Поле не заполнено"),
                Rule.DigitFormat(
                    decimalSize = 4,
                    precise = 4,
                    message = DIGIT_MESSAGE
                )
            )
        ),
        Template.Text(
            id = "kadnomer",
            label = "Кадастровый номер участка",
            format = Format.CadastralNumber,
            visual = Visual.Text()
        ),
        Template.Text(
            id = "data",
            label = "Дата создания карточки",
            visual = Visual.Date(autofill = true),
            rules = listOf(
                Rule.Required("Дата не указана")
            )
        ),
        Template.Text(
            id = "dataizm",
            label = "Дата изменения карточки",
            visual = Visual.Date(changeOnEdit = true),
            rules = listOf(
                Rule.Required("Дата не указана")
            )
        ),
        Template.Text(
            id = "cel",
            label = "Цель ВНН",
            visual = Visual.Reference(handbookId = 19),
            rules = listOf(
                Rule.Required("Поле не заполнено")
            )
        ),
        Template.Text(
            id = "datar",
            label = "Дата проведения ВНН",
            visual = Visual.Date(),
            rules = listOf(
                Rule.Required("Поле не заполнено")
            )
        ),
        Template.Text(
            id = "ekolzona",
            label = "Наименование зоны",
            visual = Visual.Reference(handbookId = 18)
        ),
        Template.Text(
            id = "nmh",
            label = "Номер маршрутного хода",
            visual = Visual.Text()
        ),
        Template.Text(
            id = "lmh",
            label = "Протяженность маршрутного хода в выделе",
            visual = Visual.Number(unit = "км"),
            rules = listOf(
                Rule.DigitFormat(
                    decimalSize = 6,
                    precise = 3,
                    message = DIGIT_MESSAGE
                )
            )
        ),
        Template.Text(
            id = "isp",
            label = "Исполнитель",
            visual = Visual.Text(),
            rules = listOf(
                Rule.Required("Исполнитель не указан")
            )
        ),
        Template.Text(
            id = "itaks1",
            label = "Информация пользователя",
            visual = Visual.Text(multiline = true)
        )
    )
)