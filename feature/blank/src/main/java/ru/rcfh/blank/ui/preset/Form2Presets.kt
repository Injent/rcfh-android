package ru.rcfh.blank.ui.preset

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.component.BasicInputComponent
import ru.rcfh.blank.ui.component.ComparisonTableComponent
import ru.rcfh.blank.ui.component.Component
import ru.rcfh.blank.ui.component.DateComponent
import ru.rcfh.blank.ui.component.InputType
import ru.rcfh.blank.ui.component.ReferenceComponent
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.queryapi.update
import ru.rcfh.blank.ui.rule.Rule
import ru.rcfh.blank.ui.search.SearchLogic
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.designsystem.component.AppCheckbox
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.icon.Add
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.glpm.feature.blank.R

fun initForm2Components(documentScope: DocumentScope) {
    listOf(
        object : ReferenceComponent(
            target = "$.forms.2.cnl",
            handbookId = 16
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Целевое назначение лесов"
        },
        object : ReferenceComponent(
            target = "$.forms.2.kzl",
            handbookId = 5
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Кат. ЗЛ"
        },
        object : ReferenceComponent(
            target = "$.forms.2.proishozdenie",
            handbookId = 24
        ) {
            override val label: String = "Происхождение насаждения"
        },
        object : ReferenceComponent(
            target = "$.forms.2.ozu",
            handbookId = 1
        ) {
            override val label: String = "ОЗУ"
        },
        object : BasicInputComponent(
            target = "$.forms.2.nozu",
            inputType = InputType.Text
        ) {
            override val label: String = "Иное ОЗУ"
        },
        object : ReferenceComponent(
            target = "$.forms.2.arenda",
            handbookId = 11
        ) {
            override val label: String = "Право пользования"
        },
        object : ReferenceComponent(
            target = "$.forms.2.vidarenda",
            handbookId = 4
        ) {
            override val label: String = "Вид использования"
        },
        object : ReferenceComponent(
            target = "$.forms.2.oopt",
            handbookId = 3
        ) {
            override val label: String = "ООПТ"
        },
        object : BasicInputComponent(
            target = "$.forms.2.noopt",
            inputType = InputType.Text
        ) {
            override val label: String = "Иное ООПТ"
        },
        object : Component(target = "$.forms.2.sootvlu") {
            @Composable
            override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
                AppCheckbox(
                    checked = state.queryOrCreate(target, "false").toBooleanStrictOrNull() ?: false,
                    onCheckedChange = { checked ->
                        state.update(target, checked.toString())
                    },
                    text = "Соответствие таксационным описаниям",
                    enabled = enabled
                )
            }
        },
        object : DateComponent(
            target = "$.forms.2.glu",
            format = "yyyy"
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Год лесоустройства"
        },
        object : ComparisonTableComponent(
            tableRootPath = "$.forms.2.plantation_common_specs",
            maxEntries = 1
        ) {
            override val name: String = "Характеристика насаждения общая"

            override fun getComponents(basePath: String, useActualPath: String): List<Component> {
                return listOf(
                    object : ReferenceComponent(
                        target = "${basePath}.katzemf",
                        handbookId = 6
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)

                        override val rules: List<Rule> = listOf(
                            Rule.Required()
                        )
                        override val label: String = "Кат. земель"
                    },
                    object : ReferenceComponent(
                        target = "${basePath}.gporodato",
                        handbookId = 31
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)

                        override val rules: List<Rule> = listOf(
                            Rule.Required()
                        )
                        override val label: String = "Главная порода"
                    },
                    object : ReferenceComponent(
                        target = "${basePath}.bonitetf",
                        handbookId = 7
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)

                        override val rules: List<Rule> = listOf(
                            Rule.Required()
                        )
                        override val label: String = "Бонитет"
                    },
                    object : BasicInputComponent(
                        target = "${basePath}.tiplesaf",
                        inputType = InputType.Text
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)
                        override val label: String = "Тип леса"
                    },
                    object : ReferenceComponent(
                        target = "tluf",
                        handbookId = 13
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)
                        override val label: String = "ТЛУ"
                    },
                    object : BasicInputComponent(
                        target = "${basePath}.zapassuhf",
                        inputType = InputType.Number
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)
                        override val rules: List<Rule> = listOf(
                            Rule.DigitFormat(
                                decimals = 6,
                                precise = 2
                            )
                        )
                        override val label: String = "Запас сухостоя, кбм на выдел"
                        override val unit: String = "м3"
                    },
                    object : BasicInputComponent(
                        target = "${basePath}.zapasredf",
                        inputType = InputType.Number
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)

                        override val rules: List<Rule> = listOf(
                            Rule.DigitFormat(
                                decimals = 6,
                                precise = 2
                            )
                        )
                        override val label: String = "Запас редин, кбм на выдел"
                        override val unit: String = "м3"
                    },
                    object : BasicInputComponent(
                        target = "${basePath}.zapasedf",
                        inputType = InputType.Number
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)

                        override val rules: List<Rule> = listOf(
                            Rule.DigitFormat(
                                decimals = 6,
                                precise = 2
                            )
                        )
                        override val label: String = "Запас един. дер., кбм на выдел"
                        override val unit: String = "м3"
                    },
                    object : BasicInputComponent(
                        target = "${basePath}.zapaszahlf",
                        inputType = InputType.Number
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)
                        override val rules: List<Rule> = listOf(
                            Rule.DigitFormat(
                                decimals = 6,
                                precise = 2
                            )
                        )
                        override val label: String = "Запас захл. общ., кбм на выдел"
                        override val unit: String = "м3"
                    },
                    object : BasicInputComponent(
                        target = "${basePath}.zapaszahllikf",
                        inputType = InputType.Number
                    ) {
                        override fun isEnabled(state: Element): Boolean =
                            isChildEnabled(state, basePath, useActualPath)
                        override val rules: List<Rule> = listOf(
                            Rule.DigitFormat(
                                decimals = 6,
                                precise = 2
                            )
                        )
                        override val label: String = "Запас захл. ликвид, кбм на выдел"
                        override val unit: String = "м3"
                    },
                )
            }
        },
        object : ComparisonTableComponent(
            tableRootPath = "$.forms.2.plantation_specs_by_layers"
        ) {
            override val name: String = "Характеристика насаждения по ярусам"

            override fun getComponents(
                basePath: String,
                useActualPath: String
            ): List<Component> = listOf(
                object : BasicInputComponent(
                    target = "${basePath}.yarushm",
                    inputType = InputType.Decimal
                ) {
                    override fun isEnabled(state: Element): Boolean =
                        isChildEnabled(state, basePath, useActualPath)
                    override val rules: List<Rule> = listOf(
                        Rule.Required(),
                        Rule.DigitFormat(decimals = 1)
                    )
                    override val label: String = "Ярус"
                },
                object : BasicInputComponent(
                    target = "${basePath}.sostavto",
                    inputType = InputType.Text
                ) {
                    override fun isEnabled(state: Element): Boolean =
                        isChildEnabled(state, basePath, useActualPath)
                    override val rules: List<Rule> = listOf(
                        Rule.Required()
                    )
                    override val label: String = "Состав насаждения"
                },
                object : BasicInputComponent(
                    target = "${basePath}.visotato",
                    inputType = InputType.Decimal
                ) {
                    override fun isEnabled(state: Element): Boolean =
                        isChildEnabled(state, basePath, useActualPath)
                    override val unit: String = "м"
                    override val rules: List<Rule> = listOf(
                        Rule.DigitFormat(decimals = 3)
                    )
                    override val label: String = "Высота яруса"
                },
                object : ReferenceComponent(
                    target = "${basePath}.klvozrto",
                    handbookId = 26
                ) {
                    override fun isEnabled(state: Element): Boolean =
                        isChildEnabled(state, basePath, useActualPath)
                    override val label: String = "Класс возраста"
                },
                object : ReferenceComponent(
                    target = "${basePath}.grvozrto",
                    handbookId = 27
                ) {
                    override fun isEnabled(state: Element): Boolean =
                        isChildEnabled(state, basePath, useActualPath)
                    override val label: String = "Группа возраста"
                },
                object : BasicInputComponent(
                    target = "${basePath}.polnota",
                    inputType = InputType.Number
                ) {
                    override fun isEnabled(state: Element): Boolean =
                        isChildEnabled(state, basePath, useActualPath)
                    override val unit: String = "0,1 доли ед."
                    override val rules: List<Rule> = listOf(
                        Rule.Required(),
                        Rule.DigitFormat(
                            decimals = 1,
                            precise = 2,
                        ),
                        Rule.Range(min = 0.0, max = 1.3)
                    )
                    override val label: String = "Полнота"
                },
                object : BasicInputComponent(
                    target = "${basePath}.zapassirgato",
                    inputType = InputType.Number
                ) {
                    override fun isEnabled(state: Element): Boolean =
                        isChildEnabled(state, basePath, useActualPath)
                    override val unit: String = "м3"
                    override val rules: List<Rule> = listOf(
                        Rule.Required(),
                        Rule.DigitFormat(
                            decimals = 6,
                            precise = 2,
                        )
                    )
                    override val label: String = "Запас сырораст., кбм на га"
                },
                object : BasicInputComponent(
                    target = "${basePath}.zapassirto",
                    inputType = InputType.Number
                ) {
                    override fun isEnabled(state: Element): Boolean =
                        isChildEnabled(state, basePath, useActualPath)
                    override val unit: String = "м3"
                    override val rules: List<Rule> = listOf(
                        Rule.Required(),
                        Rule.DigitFormat(
                            decimals = 6,
                            precise = 2,
                        )
                    )
                    override val label: String = "Запас сырораст.на выдел по составляющим породам"
                },
            )
        },
        object : ComparisonTableComponent(
            tableRootPath = "$.forms.2.species_specs"
        ) {
            override val name: String = "Характеристика породы"

            override fun getComponents(
                basePath: String,
                useActualPath: String
            ): List<Component> = listOf(
                object : BasicInputComponent(
                    target = "${basePath}.yarus",
                    inputType = InputType.Decimal
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.Required("Поле пусто"),
                        Rule.DigitFormat(decimals = 1)
                    )
                    override val label: String = "Ярус"
                },
                object : Component(
                    target = "${basePath}.dolap",
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.StrictInput(Regex("^(?:\\+|\\d*|)$")),
                        Rule.Required("Поле пусто"),
                    )

                    @Composable
                    override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
                        val bringIntoViewRequester = remember { BringIntoViewRequester() }
                        val mutableInteractionSource = remember { MutableInteractionSource() }
                        val isFocused by mutableInteractionSource.collectIsFocusedAsState()

                        LaunchedEffect(isFocused, errorMsg) {
                            if (errorMsg != null && isFocused) {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                        val value = state.queryOrCreate(target, "")

                        AppTextField(
                            label = "Доля",
                            value = value,
                            onValueChange = { newValue ->
                                if (isMetStrictRules(newValue)) {
                                    state.update(target, newValue.take(2))
                                }
                            },
                            interactionSource = mutableInteractionSource,
                            error = errorMsg,
                            enabled = enabled,
                            placeholder = stringResource(R.string.feature_blank_placeholder_decimal),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            button = {
                                if (value != "+") {
                                    AppIconButton(
                                        icon = AppIcon(
                                            icon = AppIcons.Add,
                                            tint = AppTheme.colorScheme.foreground1,
                                            onClick = {
                                                state.update(target, "+")
                                            }
                                        ),
                                    )
                                }
                            },
                            modifier = Modifier
                                .bringIntoViewRequester(bringIntoViewRequester)
                        )
                    }
                },
                object : ReferenceComponent(
                    target = "${basePath}.kodporoda",
                    handbookId = 31,
                    searchLogic = SearchLogic.SPECIES
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.Required()
                    )
                    override val label: String = "Порода"
                },
                object : BasicInputComponent(
                    target = "${basePath}.vozr",
                    inputType = InputType.Decimal
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.DigitFormat(decimals = 3)
                    )
                    override val label: String = "Ср. возраст"
                    override val unit: String = "лет"
                },
                object : BasicInputComponent(
                    target = "${basePath}.hsr",
                    inputType = InputType.Number
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.DigitFormat(decimals = 2, precise = 1)
                    )
                    override val label: String = "Ср. высота"
                    override val unit: String = "м"
                },
                object : BasicInputComponent(
                    target = "${basePath}.dsr",
                    inputType = InputType.Number
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.DigitFormat(decimals = 3, precise = 1)
                    )
                    override val label: String = "Ср. диаметр"
                    override val unit: String = "см"
                },
                object : BasicInputComponent(
                    target = "${basePath}.zapaspor",
                    inputType = InputType.Number
                ) {
                    override val rules: List<Rule> = listOf(
                        Rule.DigitFormat(decimals = 6, precise = 2)
                    )
                    override val label: String = "Запас, кбм на га"
                    override val unit: String = "м3"
                }
            )
        },
        object : BasicInputComponent(
            target = "$.forms.2.itaks2",
            inputType = InputType.Text,
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 3)
        ) {
            override val label: String = "Информация пользователя"
        }
    )
        .apply { documentScope.registerComponents(2, this) }
        .forEach { it.init(documentScope) }
}
