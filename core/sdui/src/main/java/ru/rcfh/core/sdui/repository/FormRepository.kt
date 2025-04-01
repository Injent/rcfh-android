package ru.rcfh.core.sdui.repository

import io.ktor.client.HttpClient
import ru.rcfh.core.sdui.model.FormTemplate
import ru.rcfh.core.sdui.model.Group
import ru.rcfh.core.sdui.model.Kind
import ru.rcfh.core.sdui.model.Template

class FormRepository(
    private val client: HttpClient
) {
    suspend fun sync() {

    }

    suspend fun getFormTemplates(): List<FormTemplate> {
        return listOf(
            FormTemplate(
                id = 2,
                name = "II. Форма ввода таксационной информации",
                groups = listOf(),
                templates = listOf(
                    Template.ComparisonTable(
                        id = "2comparison",
                        name = "Характ-ка насаждения",
                        repeatable = true,
                        section = listOf(
                            Template.ComparisonTable.Section(
                                name = "фактический",
                                templates = listOf(
                                    Template.Text(
                                        id = "sostavf",
                                        name = "Состав насаждения",
                                        required = true,
                                        kind = Kind.String(),
                                    ),
                                    Template.Text(
                                        id = "test1",
                                        name = "Тест",
                                        required = false,
                                        kind = Kind.Int(
                                            unit = "м",
                                            maxDigits = 3
                                        )
                                    )
                                )
                            ),
                            Template.ComparisonTable.Section(
                                name = "по материалам ТО",
                                templates = listOf(
                                    Template.Text(
                                        id = "sostavto",
                                        name = "Состав насаждения",
                                        required = true,
                                        kind = Kind.String()
                                    ),
                                    Template.Text(
                                        id = "test2",
                                        name = "Тест",
                                        required = false,
                                        kind = Kind.Int(
                                            unit = "м",
                                            maxDigits = 3
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            FormTemplate(
                id = 1,
                name = "IV. Форма ввода ВНН за санитарным состоянием насаждений глазомерным методом",
                groups = listOf(
                    Group(
                        name = "IV. Форма ввода ВНН за санитарным состоянием насаждений глазомерным методом",
                        forms = listOf("prichiniPovrezhdeniya")
                    ),
                    Group(
                        name = "Таблица",
                        forms = listOf("4table")
                    )
                ),
                templates = listOf(
                    Template.Repeatable(
                        id = "prichiniPovrezhdeniya",
                        name = "Причины повреждения",
                        templates = listOf(
                            Template.Text(
                                id = "prichina",
                                name = "Причины повреждения",
                                kind = Kind.Reference(handbookId = 1),
                                required = false
                            ),
                            Template.Text(
                                id = "godpovr",
                                name = "Год ослабления, повреждения насаждения",
                                kind = Kind.Date(format = "yyyy"),
                                required = false
                            )
                        )
                    ),
                    Template.Table(
                        id = "4table",
                        name = "Таблица 4 формы",
                        columns = listOf(
                            Template.Text(
                                id = "yarusf",
                                name = "Ярус",
                                required = true,
                                regex = "^\\d{1}\$",
                                kind = Kind.Int(
                                    maxDigits = 1
                                )
                            ),
                            Template.Text(
                                id = "dolapf",
                                name = "Доля",
                                required = true,
                                regex = "^\\d{2}\$",
                                kind = Kind.Int(
                                    maxDigits = 2
                                )
                            ),
                            Template.Text(
                                id = "kodporodaf",
                                name = "Порода",
                                required = true,
                                kind = Kind.Reference(1)
                            ),
                            Template.Text(
                                id = "vozrf",
                                name = "Ср. возраст, лет",
                                required = false,
                                regex = "^\\d{3}\$",
                                kind = Kind.Int(
                                    unit = "лет",
                                    maxDigits = 3
                                )
                            ),
                            Template.Text(
                                id = "hsrf",
                                name = "Ср.высота, м",
                                required = false,
                                regex = "^\\d{3}\\.\\d{1}\$",
                                kind = Kind.Float(
                                    unit = "м",
                                    maxIntDigits = 3,
                                    maxFractionDigits = 1
                                )
                            ),
                            Template.Text(
                                id = "dsrf",
                                name = "Ср. диаметр, см",
                                required = false,
                                regex = "^\\d{4}\\.\\d{1}\$",
                                kind = Kind.Float(
                                    unit = "см",
                                    maxIntDigits = 4,
                                    maxFractionDigits = 1
                                )
                            ),
                            Template.Text(
                                id = "bpodolap",
                                name = "I\nздор",
                                required = false,
                                regex = "^(100|[0-9]{1,2})\$",
                                placeholder = "",
                                kind = Kind.Int(
                                    unit = "%",
                                    maxDigits = 3
                                )
                            ),
                            Template.Text(
                                id = "osldolap",
                                name = "II\nослаб",
                                required = false,
                                regex = "^(100|[0-9]{1,2})\$",
                                placeholder = "",
                                kind = Kind.Int(
                                    unit = "%",
                                    maxDigits = 3
                                )
                            ),
                            Template.Text(
                                id = "sosldolap",
                                name = "III\nс.осл.",
                                required = false,
                                regex = "^(100|[0-9]{1,2})\$",
                                placeholder = "",
                                kind = Kind.Int(
                                    unit = "%",
                                    maxDigits = 3
                                )
                            ),
                            Template.Text(
                                id = "usdolap",
                                name = "IV\nусых",
                                required = false,
                                regex = "^(100|[0-9]{1,2})\$",
                                placeholder = "",
                                kind = Kind.Int(
                                    unit = "%",
                                    maxDigits = 3
                                )
                            ),
                            Template.Text(
                                id = "svsuhdolap",
                                name = "V(А)\nсв.сc.",
                                required = false,
                                regex = "^(100|[0-9]{1,2})\$",
                                placeholder = "",
                                kind = Kind.Int(
                                    unit = "%",
                                    maxDigits = 3
                                )
                            ),
                            Template.Text(
                                id = "svvtrdolap",
                                name = "V(Б)\nсв.вв.",
                                required = false,
                                regex = "^(100|[0-9]{1,2})\$",
                                placeholder = "",
                                kind = Kind.Int(
                                    unit = "%",
                                    maxDigits = 3
                                )
                            ),
                            Template.Text(
                                id = "svburdolap",
                                name = "V(В)\nсв.бл.",
                                required = false,
                                regex = "^(100|[0-9]{1,2})\$",
                                placeholder = "",
                                kind = Kind.Int(
                                    unit = "%",
                                    maxDigits = 3
                                )
                            ),
                            Template.Text(
                                id = "stsuhdolap",
                                name = "V(Г)\nст.сс.",
                                required = false,
                                regex = "^(100|[0-9]{1,2})\$",
                                placeholder = "",
                                kind = Kind.Int(
                                    unit = "%",
                                    maxDigits = 3
                                )
                            ),
                            Template.Text(
                                id = "stvtrdolap",
                                name = "V(Д)\nст.вв.",
                                required = false,
                                regex = "^(100|[0-9]{1,2})\$",
                                placeholder = "",
                                kind = Kind.Int(
                                    unit = "%",
                                    maxDigits = 3
                                )
                            ),
                            Template.Repeatable(
                                id = "4repeatable",
                                name = "Признаки повреждения по породам",
                                templates = listOf(
                                    Template.Text(
                                        id = "prizn",
                                        name = "Признак",
                                        required = false,
                                        kind = Kind.Reference(handbookId = 1)
                                    ),
                                    Template.Text(
                                        id = "dolaprizn",
                                        name = "Доля повреждённых деревьев от запаса породы",
                                        required = false,
                                        regex = "^(100|[0-9]{1,2})\$",
                                        placeholder = "",
                                        kind = Kind.Int(
                                            unit = "%",
                                            maxDigits = 3
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    Template.Text(
                        id = "charochag",
                        name = "Характер усыхания насаждения",
                        required = false,
                        kind = Kind.Reference(handbookId = 1)
                    ),
                    Template.Text(
                        id = "itaks4",
                        name = "Информация пользователя по разделу",
                        required = false,
                        kind = Kind.String(maxLines = 100)
                    )
                )
            )
        )
    }
}