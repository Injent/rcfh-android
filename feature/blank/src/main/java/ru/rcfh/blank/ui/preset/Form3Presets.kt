package ru.rcfh.blank.ui.preset

import androidx.compose.foundation.text.input.TextFieldLineLimits
import ru.rcfh.blank.ui.component.BasicInputComponent
import ru.rcfh.blank.ui.component.Component
import ru.rcfh.blank.ui.component.InputType
import ru.rcfh.blank.ui.component.ReferenceComponent
import ru.rcfh.blank.ui.component.RepeatableComponent
import ru.rcfh.blank.ui.rule.Rule
import ru.rcfh.blank.ui.state.DocumentScope

fun initForm3Components(documentScope: DocumentScope) {
    listOf(
        object : RepeatableComponent(
            rootPath = "$.forms.3.podrost",
            maxEntries = 1
        ) {
            override val name: String = "Подрост"

            override fun getComponents(basePath: String): List<Component> = listOf(
                object : BasicInputComponent(
                    target = "${basePath}.sostavpod",
                    inputType = InputType.Text,
                    onCard = true,
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.Required()
                    )
                    override val label: String = "Состав"
                },
                object : BasicInputComponent(
                    target = "${basePath}.vozrpod",
                    inputType = InputType.Decimal,
                    onCard = true,
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.Required(),
                        Rule.DigitFormat(decimals = 3)
                    )
                    override val unit: String = "лет"
                    override val label: String = "Средний возраст"
                },
                object : BasicInputComponent(
                    target = "${basePath}.hpod",
                    inputType = InputType.Number,
                    onCard = true,
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.DigitFormat(decimals = 2, precise = 1)
                    )
                    override val unit: String = "м"
                    override val label: String = "Средняя высота"
                },
                object : BasicInputComponent(
                    target = "${basePath}.gustpod",
                    inputType = InputType.Number,
                    onCard = true,
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.DigitFormat(decimals = 3, precise = 2)
                    )
                    override val unit: String = "тыс. шт./ га"
                    override val label: String = "Густота"
                },
                object : ReferenceComponent(
                    target = "${basePath}.sostpod",
                    handbookId = 23,
                    onCard = true,
                ) {
                    override val label: String = "Состояние"
                }
            )
        },
        object : BasicInputComponent(
            target = "$.forms.3.sostavpdl",
            inputType = InputType.Text
        ) {
            override val label: String = "Подлесок. Породы"
        },
        object : BasicInputComponent(
            target = "$.forms.3.gustpdl",
            inputType = InputType.Text
        ) {
            override val label: String = "Подлесок. Густота"
        },
        object : BasicInputComponent(
            target = "$.forms.3.hpdl",
            inputType = InputType.Number
        ) {
            override val rules: List<Rule> = listOf(
                Rule.DigitFormat(
                    decimals = 2,
                    precise = 1,
                )
            )
            override val label: String = "Подлесок. Средняя высота"
            override val unit: String? = "м"
        },
        object : BasicInputComponent(
            target = "$.forms.3.sostpdl",
            inputType = InputType.Text
        ) {
            override val label: String = "Подлесок. Состояние"
        },
        object : ReferenceComponent(
            target = "$.forms.3.reljeff",
            handbookId = 21
        ) {
            override val label: String = "Рельеф. Форма"
        },
        object : ReferenceComponent(
            target = "$.forms.3.reljefeksp",
            handbookId = 17
        ) {
            override val label: String = "Рельеф. Экспозиция"
        },
        object : BasicInputComponent(
            target = "$.forms.3.reljefuklon",
            inputType = InputType.Decimal,
        ) {
            override val rules: List<Rule> = listOf(
                Rule.DigitFormat(decimals = 2)
            )
            override val unit: String? = "°"
            override val label: String = "Рельеф. Уклон"
        },
        object : ReferenceComponent(
            target = "$.forms.3.eroziavid",
            handbookId = 28
        ) {
            override val label: String = "Эрозия. Вид"
        },
        object : ReferenceComponent(
            target = "$.forms.3.eroziastep",
            handbookId = 22
        ) {
            override val label: String = "Эрозия. Степень"
        },
        object : ReferenceComponent(
            target = "$.forms.3.provlhm",
            handbookId = 32
        ) {
            override val label: String = "Проведенные хозяйственные мероприятия"
        },
        object : BasicInputComponent(
            target = "$.forms.3.itaks3",
            inputType = InputType.Text,
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 3)
        ) {
            override val label: String = "Информация пользователя"
        }
    )
        .apply { documentScope.registerComponents(3, this) }
        .forEach { it.init(documentScope) }
}