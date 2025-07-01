package ru.rcfh.feature.forms.presentation.composables

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import ru.rcfh.common.now
import ru.rcfh.core.sdui.model.Kind
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.designsystem.component.AppCheckbox
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.component.DateTextField
import ru.rcfh.designsystem.component.ReferenceTextField
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.forms.R
import ru.rcfh.feature.forms.state.LocalSavedStateHandle
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen
import java.util.UUID

@Composable
fun TextComposable(
    value: String,
    onValueChange: (String) -> Unit,
    template: Template.Text,
    modifier: Modifier = Modifier,
    onCard: Boolean = false,
) {
    val context = LocalContext.current
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val textFieldState = remember { TextFieldState(initialText = value) }

    LaunchedEffect(value) {
        if (value != textFieldState.text.toString()) {
            textFieldState.edit { replace(0, length, value) }
        }
    }

    LaunchedEffect(textFieldState.text) {
        val newText = textFieldState.text.toString()

        if (newText != value) {
            onValueChange(newText.trim())
        }

        if (template.required) {
            if (newText.isBlank()) {
                errorMsg = context.getString(R.string.error_emptyValue)
                return@LaunchedEffect
            } else {
                errorMsg = null
            }
        }

        template.regex?.let { exp ->
            if (newText.isBlank() && !template.required) return@LaunchedEffect
            if (!exp.toRegex().matches(newText)) {
                errorMsg = template.errorMsg ?: context.getString(R.string.error_invalidValue)
            } else if (errorMsg != null) {
                errorMsg = null
            }
        }
    }

    val theModifier = modifier
        .widthIn(max = 350.dp)

    when (val kind = template.kind) {
        is Kind.Unary -> Unit
        is Kind.Bool -> {
            AppCheckbox(
                checked = textFieldState.text.toString().toBooleanStrictOrNull() ?: false,
                onCheckedChange = { checked ->
                    textFieldState.setTextAndPlaceCursorAtEnd(checked.toString())
                },
                text = template.name,
                modifier = theModifier
            )
        }

        is Kind.Date -> {
            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                focusManager.clearFocus()
                if (textFieldState.text.isEmpty() && kind.autofill) {
                    textFieldState.setTextAndPlaceCursorAtEnd(
                        LocalDate.now().format(kind.format)
                    )
                }
            }

            DateTextField(
                state = textFieldState,
                errorMsg = errorMsg,
                format = kind.format,
                label = template.name,
                onCard = onCard,
                placeholder = template.placeholder,
                modifier = theModifier
            )
        }

        is Kind.Float -> {
            AppTextField(
                state = textFieldState,
                label = template.name,
                error = errorMsg,
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    kind.unit?.let {
                        Text(
                            text = it,
                            style = AppTheme.typography.callout
                        )
                    }
                },
                inputTransformation = {
                    val text = asCharSequence()
                    if (text.startsWith('.')) {
                        revertAllChanges()
                    }

                    val dotsCount = text.count { it == '.' }
                    if (dotsCount > 1) {
                        revertAllChanges()
                    }
                    val parts = text.split('.')
                    if (parts[0].length == kind.maxIntDigits?.plus(1) && dotsCount == 0) {
                        insert(parts[0].length - 1, ".")
                    }
                    if (parts.size == 2
                        && (parts[0].length > (kind.maxIntDigits ?: Int.MAX_VALUE)
                                || parts[1].length > (kind.maxFractionDigits ?: Int.MAX_VALUE))
                    ) {
                        revertAllChanges()
                    }
                    if (!text.all { it.isDigit() || it == '.' }) {
                        revertAllChanges()
                    }
                },
                placeholder = template.placeholder,
                digitPlaceholder = template.placeholder.isEmpty(),
                onCard = onCard,
                modifier = theModifier
            )
        }

        is Kind.Int -> {
            AppTextField(
                state = textFieldState,
                label = template.name,
                error = errorMsg,
                onKeyboardAction = {
                    try {
                        focusManager.moveFocus(FocusDirection.Next)
                    } catch (e: IllegalStateException) {
                        // Focusable composable not found
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    kind.unit?.let {
                        Text(
                            text = it,
                            style = AppTheme.typography.callout
                        )
                    }
                },
                inputTransformation = {
                    if (asCharSequence().length > (kind.maxDigits ?: Int.MAX_VALUE)) {
                        revertAllChanges()
                    }
                    if (!asCharSequence().all { it.isDigit() }) {
                        revertAllChanges()
                    }
                },
                onCard = onCard,
                placeholder = template.placeholder,
                digitPlaceholder = true,
                modifier = theModifier
            )
        }

        is Kind.Reference -> {
            val scope = rememberCoroutineScope()
            val resultKey = rememberSaveable { UUID.randomUUID().toString() }
            val savedStateHandle = LocalSavedStateHandle.current
            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                focusManager.clearFocus()
                savedStateHandle.get<String>(resultKey)?.let {
                    textFieldState.setTextAndPlaceCursorAtEnd(it)
                }
            }

            ReferenceTextField(
                state = textFieldState,
                label = template.name,
                errorMsg = errorMsg,
                placeholder = stringResource(R.string.placeholder_reference),
                onClick = {
                    scope.launch {
                        Navigator.navigate(
                            Screen.HandbookSearch(
                                handbookId = kind.handbookId,
                                resultKey = resultKey
                            )
                        )
                    }
                },
                onCard = onCard,
                modifier = theModifier
            )
        }

        is Kind.String -> {
            AppTextField(
                state = textFieldState,
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                error = errorMsg,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                label = template.name,
                placeholder = stringResource(R.string.placeholder_input),
                lineLimits = if (kind.minLines > 1) {
                    TextFieldLineLimits.MultiLine(
                        maxHeightInLines = kind.minLines,
                        minHeightInLines = kind.minLines.coerceAtMost(6)
                    )
                } else TextFieldLineLimits.SingleLine,
                onCard = onCard,
                modifier = theModifier
            )
        }
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
private fun LocalDate.format(format: String): String {
    return format(LocalDate.Format {
        byUnicodePattern(format)
    })
}