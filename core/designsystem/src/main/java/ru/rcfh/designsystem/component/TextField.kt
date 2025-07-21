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
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Calendar
import ru.rcfh.designsystem.icon.Handbook
import ru.rcfh.designsystem.theme.AppTheme

@Composable
fun ReferenceTextField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String? = null,
    label: String? = null,
    enabled: Boolean = true,
    onCard: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = {},
        interactionSource = interactionSource,
        textStyle = AppTheme.typography.body.copy(color = AppTheme.colorScheme.foreground1),
        readOnly = true,
        visualTransformation = VisualTransformation.None,
        decorationBox = { innerTextField ->
            TextFieldDecorator(
                innerTextField = innerTextField,
                label = label,
                isFocused = isFocused,
                onCard = onCard,
                lineLimits = TextFieldLineLimits.MultiLine(),
                placeholder = placeholder,
                isEmpty = value.isEmpty(),
                error = error,
                button = {
                    AppIconButton(
                        icon = AppIcon(
                            icon = AppIcons.Handbook,
                            onClick = {
                                if (enabled) onClick()
                            },
                        )
                    )
                }
            )
        },
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .onFocusChanged { state ->
                if (state.isFocused) {
                    onClick()
                }
            }
    )
}

private const val YEAR_FORMAT = "yyyy"

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun DateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    format: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    onCard: Boolean = false,
    error: String? = null,
    readOnly: Boolean = false,
    placeholder: String = "",
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var dialogOpen by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = {},
        interactionSource = interactionSource,
        textStyle = AppTheme.typography.body.copy(color = AppTheme.colorScheme.foreground1),
        readOnly = true,
        visualTransformation = VisualTransformation.None,
        decorationBox = { innerTextField ->
            TextFieldDecorator(
                innerTextField = innerTextField,
                label = label,
                isFocused = isFocused,
                onCard = onCard,
                placeholder = placeholder,
                button = {
                    AppIconButton(
                        icon = AppIcon(
                            icon = AppIcons.Calendar,
                            onClick = { dialogOpen = true },
                        )
                    )
                },
                error = error,
                isEmpty = value.isEmpty(),
                modifier = Modifier
                    .clickable {
                        if (!readOnly) {
                            dialogOpen = true
                        }
                    }
            )
        },
        modifier = modifier
            .onFocusChanged { focusState ->
                if (focusState.isFocused && !readOnly) {
                    dialogOpen = true
                }
            }
            .focusable()
    )

    if (dialogOpen) {
        if (format.equals(YEAR_FORMAT, ignoreCase = true)) {
            YearPickerDialog(
                onSelect = { year ->
                    onValueChange(year.toString())
                    dialogOpen = false
                },
                onDismiss = {
                    dialogOpen = false
                }
            )
        } else {
            AppCalendarDialog(
                onDismissRequest = { dialogOpen = false },
                onSelectDate = { date ->
                    onValueChange(date.format(LocalDate.Format { byUnicodePattern(format) }))
                    dialogOpen = false
                }
            )
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    inputTransformation: (String) -> Boolean = { true },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: @Composable () -> Unit = {},
    onCard: Boolean = false,
    label: String? = null,
    error: String? = null,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    button: @Composable (() -> Unit)? = null,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = {
            if (inputTransformation(it)) {
                onValueChange(it)
            }
        },
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = AppTheme.typography.body.copy(color = AppTheme.colorScheme.foreground1),
        minLines = when (lineLimits) {
            is TextFieldLineLimits.MultiLine -> lineLimits.minHeightInLines
            TextFieldLineLimits.SingleLine -> 1
        },
        maxLines = when (lineLimits) {
            is TextFieldLineLimits.MultiLine -> lineLimits.maxHeightInLines
            TextFieldLineLimits.SingleLine -> 1
        },
        readOnly = readOnly,
        visualTransformation = VisualTransformation.None,
        decorationBox = { innerTextField ->
            TextFieldDecorator(
                innerTextField = innerTextField,
                label = label,
                trailingIcon = trailingIcon,
                isFocused = isFocused,
                leadingIcon = leadingIcon,
                onCard = onCard,
                placeholder = placeholder,
                lineLimits = lineLimits,
                isEmpty = value.isEmpty(),
                button = button,
                error = error,
            )
        },
        modifier = modifier
    )
}

@Composable
fun AppTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    inputTransformation: (String) -> Boolean = { true },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: @Composable () -> Unit = {},
    onCard: Boolean = false,
    label: String? = null,
    error: String? = null,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    button: @Composable (() -> Unit)? = null,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = {
            if (inputTransformation(it.text)) {
                onValueChange(it)
            }
        },
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = AppTheme.typography.body.copy(color = AppTheme.colorScheme.foreground1),
        minLines = when (lineLimits) {
            is TextFieldLineLimits.MultiLine -> lineLimits.minHeightInLines
            TextFieldLineLimits.SingleLine -> 1
        },
        maxLines = when (lineLimits) {
            is TextFieldLineLimits.MultiLine -> lineLimits.maxHeightInLines
            TextFieldLineLimits.SingleLine -> 1
        },
        readOnly = readOnly,
        visualTransformation = VisualTransformation.None,
        decorationBox = { innerTextField ->
            TextFieldDecorator(
                innerTextField = innerTextField,
                label = label,
                trailingIcon = trailingIcon,
                isFocused = isFocused,
                leadingIcon = leadingIcon,
                onCard = onCard,
                placeholder = placeholder,
                lineLimits = lineLimits,
                isEmpty = value.text.isEmpty(),
                button = button,
                error = error,
            )
        },
        modifier = modifier
    )
}

@Composable
fun AppTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String? = null,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: @Composable () -> Unit = {},
    onCard: Boolean = false,
    readOnly: Boolean = false,
    label: String? = null,
    button: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        state = state,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        outputTransformation = outputTransformation,
        decorator = { innerTextField ->
            TextFieldDecorator(
                innerTextField = innerTextField,
                label = label,
                trailingIcon = trailingIcon,
                isFocused = isFocused,
                leadingIcon = leadingIcon,
                onCard = onCard,
                lineLimits = lineLimits,
                placeholder = placeholder,
                button = button,
                isEmpty = state.text.isEmpty(),
                error = error
            )
        },
        readOnly = readOnly,
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
    onCard: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
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
                innerTextField = innerTextField,
                label = label,
                trailingIcon = trailingIcon,
                isFocused = isFocused,
                leadingIcon = leadingIcon,
                onCard = onCard,
                placeholder = placeholder,
                isEmpty = state.text.isEmpty(),
                error = error
            )
        },
        textStyle = AppTheme.typography.body.copy(color = AppTheme.colorScheme.foreground1),
        inputTransformation = inputTransformation,
        modifier = modifier,
    )
}

@Composable
private fun TextFieldDecorator(
    isEmpty: Boolean,
    innerTextField: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    button: (@Composable () -> Unit)? = null,
    trailingIcon: @Composable () -> Unit = {},
    leadingIcon: (@Composable () -> Unit)? = null,
    isFocused: Boolean = false,
    onCard: Boolean = false,
    placeholder: String = "",
    error: String? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
) {
    Column(modifier) {
        label?.let {
            val subtitleColor = AppTheme.colorScheme.foreground2
            Text(
                text = buildAnnotatedString {
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
            val borderWidth = remember(error, isFocused) {
                when {
                    error != null -> 1.2.dp
                    isFocused -> 1.dp
                    else -> 0.dp
                }
            }

            val borderColor = when {
                error != null -> AppTheme.colorScheme.foregroundError
                isFocused -> AppTheme.colorScheme.stroke1
                else -> Color.Transparent
            }

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
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = AppTheme.shapes.small
                    )
                    .padding(horizontal = AppTheme.spacing.m)
                    .weight(1f)
            ) {
                leadingIcon?.let { icon ->
                    Box(
                        modifier = Modifier
                            .padding(end = AppTheme.spacing.xs)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides AppTheme.colorScheme.foreground2
                        ) {
                            icon()
                        }
                    }
                }

                Box(
                    contentAlignment = when (lineLimits) {
                        is TextFieldLineLimits.MultiLine -> Alignment.TopStart
                        TextFieldLineLimits.SingleLine -> Alignment.CenterStart
                    },
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (lineLimits is TextFieldLineLimits.MultiLine) {
                                Modifier.padding(vertical = AppTheme.spacing.mNudge)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    innerTextField()
                    Text(
                        text = placeholder,
                        style = AppTheme.typography.body,
                        color = AppTheme.colorScheme.foreground4,
                        modifier = Modifier.alpha(
                            if (isEmpty) 1f else 0f
                        )
                    )
                }

                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colorScheme.foreground2
                ) {
                    trailingIcon()
                }
            }
            button?.invoke()
        }

        error?.let {
            Text(
                text = error,
                style = AppTheme.typography.callout,
                color = AppTheme.colorScheme.foregroundError,
                modifier = Modifier
                    .padding(top = AppTheme.spacing.xs)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppCalendarDialog(
    onDismissRequest: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = AppTheme.colorScheme.background1,
            background = AppTheme.colorScheme.background1,
            onSurface = AppTheme.colorScheme.foreground1,
            primary = AppTheme.colorScheme.foreground,
            onPrimary = AppTheme.colorScheme.foregroundOnBrand,
            secondaryContainer = AppTheme.colorScheme.backgroundBrand,
            surfaceVariant = AppTheme.colorScheme.background4
        )
    ) {
        val state = rememberDatePickerState()
        val confirmButtonEnabled by remember {
            derivedStateOf { state.selectedDateMillis != null }
        }

        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            colors = DatePickerDefaults.colors(
                containerColor = AppTheme.colorScheme.background1,

            ),
            confirmButton = {
                AppSmallButton(
                    text = stringResource(android.R.string.ok),
                    onClick = {
                        state.selectedDateMillis?.let { dateMillis ->
                            Instant
                                .fromEpochMilliseconds(dateMillis)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .let { onSelectDate(it.date) }
                        }
                    },
                    enabled = confirmButtonEnabled
                )
            },
            dismissButton = {
                AppTextButton(
                    text = stringResource(android.R.string.cancel),
                    onClick = onDismissRequest
                )
            }
        ) {
            DatePicker(state = state)
        }
    }
}