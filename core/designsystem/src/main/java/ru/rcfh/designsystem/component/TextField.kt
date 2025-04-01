package ru.rcfh.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toKotlinLocalDate
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Calendar
import ru.rcfh.designsystem.icon.Handbook
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf

@Composable
fun ReferenceTextField(
    state: TextFieldState,
    onClick: () -> Unit,
    placeholder: String = "",
    errorMsg: String? = null,
    label: String? = null,
    onCard: Boolean = false,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val lineLimits = TextFieldLineLimits.MultiLine()

    BasicTextField(
        state = state,
        interactionSource = interactionSource,
        textStyle = AppTheme.typography.body.copy(color = AppTheme.colorScheme.foreground1),
        lineLimits = lineLimits,
        decorator = { innerTextField ->
            TextFieldDecorator(
                state = state,
                innerTextField = innerTextField,
                label = label,
                errorMsg = errorMsg,
                placeholder = placeholder,
                isFocused = isFocused,
                onCard = onCard,
                lineLimits = lineLimits,
                button = {
                    Surface(
                        shape = AppTheme.shapes.default,
                        color = if (onCard) {
                            AppTheme.colorScheme.background2
                        } else AppTheme.colorScheme.background4,
                        onClick = onClick,
                        modifier = Modifier
                            .size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = AppIcons.Handbook,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.foreground,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        }
                    }
                }
            )
        },
        readOnly = true,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .onFocusChanged { state ->
                if (state.isFocused) {
                    onClick()
                }
            }
    )
}

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun DateTextField(
    state: TextFieldState,
    onValueChange: () -> Unit,
    format: String,
    label: String? = null,
    errorMsg: String? = null,
    onCard: Boolean = false,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val calendarState = rememberUseCaseState()

    BasicTextField(
        state = state,
        interactionSource = interactionSource,
        textStyle = AppTheme.typography.body.copy(color = AppTheme.colorScheme.foreground1),
        lineLimits = TextFieldLineLimits.SingleLine,
        decorator = { innerTextField ->
            TextFieldDecorator(
                state = state,
                innerTextField = innerTextField,
                label = label,
                errorMsg = errorMsg,
                placeholder = placeholder,
                isFocused = isFocused,
                onCard = onCard,
                button = {
                    Surface(
                        shape = AppTheme.shapes.default,
                        color = if (onCard) {
                            AppTheme.colorScheme.background2
                        } else AppTheme.colorScheme.background4,
                        onClick = calendarState::show,
                        modifier = Modifier
                            .size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = AppIcons.Calendar,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.foreground,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        }
                    }
                }
            )
        },
        readOnly = true,
        modifier = modifier
            .focusable()
            .onFocusChanged { state ->
                if (state.isFocused) {
                    calendarState.show()
                }
            }
    )

    AppCalendarDialog(
        state = calendarState,
        format = format,
        onSelectDate = { date ->
            onValueChange()
            state.setTextAndPlaceCursorAtEnd(
                date.format(LocalDate.Format { byUnicodePattern(format) })
            )
            calendarState.finish()
        }
    )
}

@Composable
fun AppTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String? = null,
    inputTransformation: InputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    onCard: Boolean = false,
    label: String? = null,
    digitPlaceholder: Boolean = false,
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    val isFocused by mutableInteractionSource.collectIsFocusedAsState()

    BasicTextField(
        state = state,
        interactionSource = mutableInteractionSource,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        decorator = { innerTextField ->
            TextFieldDecorator(
                state = state,
                innerTextField = innerTextField,
                label = label,
                trailingIcon = trailingIcon,
                placeholder = placeholder,
                errorMsg = error,
                isFocused = isFocused,
                leadingIcon = leadingIcon,
                onCard = onCard,
                digitPlaceholder = digitPlaceholder,
                lineLimits = lineLimits
            )
        },
        textStyle = AppTheme.typography.body.copy(color = AppTheme.colorScheme.foreground1),
        inputTransformation = inputTransformation,
        lineLimits = lineLimits,
        modifier = modifier,
    )
}

@Composable
fun AppSecureTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable () -> Unit = {},
    placeholder: String = "",
    error: String? = null,
    inputTransformation: InputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    label: String? = null
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    val isFocused by mutableInteractionSource.collectIsFocusedAsState()

    BasicSecureTextField(
        state = state,
        interactionSource = mutableInteractionSource,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        decorator = { innerTextField ->
            TextFieldDecorator(
                state = state,
                innerTextField = innerTextField,
                label = label,
                trailingIcon = trailingIcon,
                placeholder = placeholder,
                errorMsg = error,
                isFocused = isFocused
            )
        },
        textStyle = AppTheme.typography.body.copy(color = AppTheme.colorScheme.foreground1),
        inputTransformation = inputTransformation,
        modifier = modifier,
    )
}

@Composable
private fun TextFieldDecorator(
    state: TextFieldState,
    innerTextField: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    button: (@Composable () -> Unit)? = null,
    trailingIcon: @Composable () -> Unit = {},
    leadingIcon: @Composable () -> Unit = {},
    placeholder: String = "",
    errorMsg: String? = null,
    isFocused: Boolean = false,
    onCard: Boolean = false,
    digitPlaceholder: Boolean = false,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
) {
    Column(modifier) {
        label?.let {
            val subtitleColor = AppTheme.colorScheme.foreground2
            Text(
                text = remember(label) {
                    buildAnnotatedString {
                        addStyle(
                            style = SpanStyle(fontSize = 11.sp),
                            start = label.indexOfFirst { it == '(' }.takeIf { it != -1 } ?: 0,
                            end = label.indexOfLast { it == ')' }.takeIf { it != -1 }?.plus(1) ?: 0
                        )

                        if (label.count { it == '\n' } == 1) {
                            val (title, subtitle) = label.split('\n')

                            append(title)
                            withStyle(
                                SpanStyle(
                                    color = subtitleColor,
                                    fontSize = 12.sp
                                )
                            ) {
                                appendLine()
                                append(subtitle)
                            }
                        } else {
                            append(label)
                        }
                    }
                },
                style = AppTheme.typography.callout.copy(lineHeight = 12.sp),
                color = AppTheme.colorScheme.foreground1,
                modifier = Modifier
                    .padding(bottom = AppTheme.spacing.sNudge)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.m)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp)
                    .background(
                        if (onCard) {
                            AppTheme.colorScheme.background2
                        } else AppTheme.colorScheme.background4,
                        AppTheme.shapes.small
                    )
                    .thenIf(errorMsg != null) {
                        border(
                            width = 1.2.dp,
                            color = AppTheme.colorScheme.foregroundError,
                            shape = AppTheme.shapes.small
                        )
                    }
                    .thenIf(isFocused && errorMsg == null) {
                        border(
                            width = 1.dp,
                            color = AppTheme.colorScheme.stroke1,
                            shape = AppTheme.shapes.small
                        )
                    }
                    .padding(horizontal = AppTheme.spacing.m)
                    .weight(1f)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colorScheme.foreground2
                ) {
                    leadingIcon()
                }
                Box(
                    contentAlignment = when (lineLimits) {
                        is TextFieldLineLimits.MultiLine -> Alignment.TopStart
                        TextFieldLineLimits.SingleLine -> Alignment.CenterStart
                    },
                    modifier = Modifier
                        .weight(1f)
                        .thenIf(lineLimits is TextFieldLineLimits.MultiLine) {
                            padding(vertical = AppTheme.spacing.mNudge)
                        }
                ) {
                    innerTextField()

                    if (digitPlaceholder) {
                        Text(
                            text = remember(state.text, placeholder) {
                                buildAnnotatedString {
                                    addStyle(
                                        style = SpanStyle(color = Color.Transparent),
                                        start = 0,
                                        end = state.text.length.coerceAtMost(placeholder.length)
                                    )
                                    append(placeholder)
                                }
                            },
                            style = AppTheme.typography.body,
                            minLines = 1,
                            color = AppTheme.colorScheme.foreground4,
                        )
                    } else if (state.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = AppTheme.typography.body,
                            minLines = 1,
                            color = AppTheme.colorScheme.foreground4,
                        )
                    }

                }

                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colorScheme.foreground2
                ) {
                    trailingIcon()
                }
            }
            button?.invoke()
        }

        errorMsg?.let {
            Text(
                text = it,
                style = AppTheme.typography.footnote,
                color = AppTheme.colorScheme.foregroundError,
                modifier = Modifier
                    .padding(top = AppTheme.spacing.xxs)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppCalendarDialog(
    state: UseCaseState,
    format: String,
    onSelectDate: (LocalDate) -> Unit,
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = AppTheme.colorScheme.background1,
            onSurface = AppTheme.colorScheme.foreground1,
            primary = AppTheme.colorScheme.foreground,
            onPrimary = AppTheme.colorScheme.foregroundOnBrand,
            secondaryContainer = AppTheme.colorScheme.backgroundBrand,
            surfaceVariant = AppTheme.colorScheme.background4
        )
    ) {
        CalendarDialog(
            state = state,
            selection = CalendarSelection.Date(
                selectedDate = java.time.LocalDate.now(),
                onSelectDate = {
                    onSelectDate(it.toKotlinLocalDate())
                }
            ),
            config = CalendarConfig(
                yearSelection = format.lowercase() == "yyyy",
                style = CalendarStyle.MONTH,
            ),
        )
    }
}