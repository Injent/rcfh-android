package ru.rcfh.blank.ui.preset

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.component.BasicInputComponent
import ru.rcfh.blank.ui.component.Component
import ru.rcfh.blank.ui.component.DateComponent
import ru.rcfh.blank.ui.component.InputType
import ru.rcfh.blank.ui.component.ReferenceComponent
import ru.rcfh.blank.ui.queryapi.query
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.queryapi.queryOrDefault
import ru.rcfh.blank.ui.queryapi.update
import ru.rcfh.blank.ui.rule.OnlyDecimal
import ru.rcfh.blank.ui.rule.OnlyNumber
import ru.rcfh.blank.ui.rule.Rule
import ru.rcfh.blank.ui.search.FilterLogic
import ru.rcfh.blank.ui.search.SearchLogic
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.blank.ui.style.LocationStyle
import ru.rcfh.blank.ui.style.OnLocationChange
import ru.rcfh.common.now
import ru.rcfh.designsystem.component.AppTextField

fun initForm1Components(documentScope: DocumentScope) {
    listOf(
        object : Component(target = "$.forms.1.lat") {
            override val rules: List<Rule> = persistentListOf(
                Rule.StrictInput(Regex.OnlyNumber),
                Rule.Required(),
                Rule.DigitFormat(3, 6),
            )
            @Composable
            override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
                val lon = state.queryOrCreate("$.forms.1.lon", "")
                LocationStyle(
                    lat = state.queryOrCreate("$.forms.1.lat", ""),
                    lon = lon,
                    latError = errorMsg,
                    lonError = if (lon.isBlank()) "Ошибка в значении" else null,
                    onChange = object : OnLocationChange {
                        override fun onLatitudeChange(lat: String) {
                            state.update("$.forms.1.lat", lat)
                        }
                        override fun onLongitudeChange(lon: String) {
                            state.update("$.forms.1.lon", lon)
                        }
                        override fun onMslChange(msl: String) {
                            state.update("$.forms.1.vnum", msl)
                        }
                    }
                )
            }
        },
        object : BasicInputComponent(
            target = "$.forms.1.vnum",
            inputType = InputType.Decimal
        ) {
            override val rules: List<Rule> = listOf(
                Rule.StrictInput(Regex.OnlyDecimal),
                Rule.DigitFormat(decimals = 5)
            )
            override val label: String = "ВНУМ"
        },
        object : ReferenceComponent(
            target = "$.forms.1.filial",
            searchLogic = SearchLogic.BASIC,
            handbookId = 15
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Организация, проводившая работу"
        },
        object : ReferenceComponent(
            target = "$.forms.1.srf",
            searchLogic = SearchLogic.BASIC,
            handbookId = 12
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Субъект Российской Федерации"
        },
        object : ReferenceComponent(
            target = "$.forms.1.lvo",
            handbookId = 36,
            filterLogic = { state, reference ->
                reference.parentId == state.query("$.forms.1.srf.id")?.intOrNull
            }
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Лесничество"
            override fun isEnabled(state: Element): Boolean {
                val srf = state.query("$.forms.1.srf")
                return !(srf == null || srf.query("$.id")?.textOrEmpty == null)
            }
            override fun calculate(documentScope: DocumentScope, state: Element) {
                val dependsOn = state.query("$.forms.1.srf.id")?.intOrNull
                val parentId = state.query("${rootTarget}.parent_id")?.intOrNull
                if (dependsOn != parentId) {
                    state.update(rootTarget, null)
                }
            }
        },
        object : ReferenceComponent(
            target = "$.forms.1.ulvo",
            handbookId = 37,
            filterLogic = FilterLogic { state, reference ->
                reference.parentId == state.query("$.forms.1.lvo.id")?.intOrNull
            }
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Уч. лесничество"
            override fun isEnabled(state: Element): Boolean {
                val lvo = state.query("$.forms.1.lvo")
                return !(lvo == null || lvo.query("$.id")?.textOrEmpty == null)
            }
            override fun calculate(documentScope: DocumentScope, state: Element) {
                val dependsOn = state.query("$.forms.1.lvo.id")?.intOrNull
                val parentId = state.query("${rootTarget}.parent_id")?.intOrNull
                if (dependsOn != parentId) {
                    state.update(rootTarget, null)
                }
            }
        },
        object : ReferenceComponent(
            target = "$.forms.1.urc",
            handbookId = 38,
            filterLogic = FilterLogic { state, reference ->
                reference.parentId == state.query("$.forms.1.ulvo.id")?.intOrNull
            }
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Урочище (лесная дача и т.п.)"
            override fun isEnabled(state: Element): Boolean {
                val ulvo = state.query("$.forms.1.ulvo")
                return !(ulvo == null || ulvo.query("$.id")?.textOrEmpty == null)
            }
            override fun calculate(documentScope: DocumentScope, state: Element) {
                val dependsOn = state.query("$.forms.1.ulvo.id")?.intOrNull
                val parentId = state.query("${rootTarget}.parent_id")?.intOrNull
                if (dependsOn != parentId) {
                    state.update(rootTarget, null)
                }
            }
        },
        object : BasicInputComponent(
            target = "$.forms.1.kv",
            inputType = InputType.Text
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Квартал"
        },
        object : BasicInputComponent(
            target = "$.forms.1.vid",
            inputType = InputType.Text
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Выдел"
        },
        object : BasicInputComponent(
            target = "$.forms.1.s",
            inputType = InputType.Number
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required(),
                Rule.DigitFormat(decimals = 4, precise = 4),
            )
            override val label: String = "Площадь лесотаксационного выдела"
            override val unit: String = "га"
        },
        object : BasicInputComponent(
            target = "$.forms.lpvid",
            inputType = InputType.Text
        ) {
            override val label: String = "Номер лесопатологического выдела"
        },
        object : BasicInputComponent(
            target = "$.forms.slp",
            inputType = InputType.Number,
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required(),
                Rule.DigitFormat(
                    decimals = 4,
                    precise = 4
                )
            )
            override val label: String = "Площадь лесотаксационного выдела или лесопатологического выдела (при его выделении)"
            override val unit: String = "га"
            override fun findError(state: Element, s: String?) {
                super.findError(state, s)
                val fullnessLimit = state.queryOrDefault("$.forms.1.s", 0)
                if (state.queryOrDefault(target, 0) > fullnessLimit) {
                    errorMsg = "Превышает площадь лесотаксационного выдела"
                }
            }
        },
        object : BasicInputComponent(
            target = "$.forms.1.kadnomer",
            inputType = InputType.Text,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        ) {
            override val label: String = "Кадастровый номер участка"
            override val placeholder: String = "00:00:0000000:0000"
            override val rules: List<Rule> = listOf(
                Rule.Regex("(\\d{2}:\\d{2}:\\d{7}:\\d{4})|^$".toRegex(), "Номер не полностью написан")
            )
            @Composable
            override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
                val textState = rememberTextFieldState(state.queryOrDefault(target, ""))
                LaunchedEffect(Unit) {
                    snapshotFlow { textState.text }
                        .collect {
                            state.update(target, it.toString())
                        }
                }
                AppTextField(
                    state = textState,
                    inputTransformation = {
                        val digits = asCharSequence().filter { it.isDigit() }
                        val maxLength = 18 // 2 + 1 + 2 + 1 + 7 + 1 + 4 + 3
                        val formatted = when {
                            digits.isEmpty() -> ""
                            digits.length <= 2 -> digits
                            digits.length <= 4 -> "${digits.take(2)}:${digits.drop(2)}"
                            digits.length <= 11 -> "${digits.take(2)}:${digits.drop(2).take(2)}:${digits.drop(4)}"
                            digits.length <= 15 -> "${digits.take(2)}:${digits.drop(2).take(2)}:${digits.drop(4).take(7)}:${digits.drop(11)}"
                            else -> "${digits.take(2)}:${digits.drop(2).take(2)}:${digits.drop(4).take(7)}:${digits.drop(11).take(4)}:${digits.drop(15).take(3)}"
                        }.take(maxLength)
                        replace(0, length, formatted)
                    },
                    keyboardOptions = keyboardOptions,
                    label = label,
                    placeholder = placeholder,
                    lineLimits = lineLimits,
                    error = errorMsg,
                    enabled = enabled,
                    onCard = onCard,
                )
            }
        },
        object : DateComponent(
            target = "$.forms.1.data"
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Дата создания карточки"
            @OptIn(FormatStringsInDatetimeFormats::class)
            override fun calculate(documentScope: DocumentScope, state: Element) {
                if (state.query(target) != null) return
                val currentDateString = LocalDate.now()
                    .format(LocalDate.Format { byUnicodePattern(format) })
                state.update(target, currentDateString)
            }
        },
        object : DateComponent(
            target = "$.forms.1.dataizm"
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Дата изменения карточки"
            @OptIn(FormatStringsInDatetimeFormats::class)
            override fun calculate(documentScope: DocumentScope, state: Element) {
                val currentDateString = LocalDate.now()
                    .format(LocalDate.Format { byUnicodePattern(format) })
                state.update(target, currentDateString)
            }
        },
        object : ReferenceComponent(
            target = "$.forms.1.cel",
            handbookId = 19
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Цель ВНН"
        },
        object : DateComponent(
            target = "$.forms.1.datar",
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Дата проведения ВНН"
        },
        object : ReferenceComponent(
            target = "$.forms.1.ekolzona",
            handbookId = 18
        ) {
            override val label: String = "Наименование зоны"
        },
        object : BasicInputComponent(
            target = "$.forms.1.nmh",
            inputType = InputType.Text
        ) {
            override val label: String = "Номер маршрутного хода"
        },
        object : BasicInputComponent(
            target = "$.forms.1.lmh",
            inputType = InputType.Number
        ) {
            override val rules: List<Rule> = listOf(
                Rule.DigitFormat(decimals = 6, precise = 3)
            )
            override val unit: String = "км"
            override val label: String = "Протяженность маршрутного хода в выделе"
        },
        object : BasicInputComponent(
            target = "$.forms.1.isp",
            inputType = InputType.Text,
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 2)
        ) {
            override val rules: List<Rule> = listOf(
                Rule.Required()
            )
            override val label: String = "Исполнитель"
        },
        object : BasicInputComponent(
            target = "$.forms.1.itaks1",
            inputType = InputType.Text,
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 3)
        ) {
            override val label: String = "Информация пользователя"
        }
    )
        .apply { 
            documentScope.registerComponents(1, this)
        }
        .forEach {
            it.init(documentScope)
        }
}
