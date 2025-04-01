package ru.rcfh.feature.forms.component

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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rcfh.core.sdui.compose.LocalStateCache
import ru.rcfh.core.sdui.model.Kind
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.designsystem.component.AppCheckbox
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.component.DateTextField
import ru.rcfh.designsystem.component.ReferenceTextField
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.forms.R
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

@Composable
fun TextInput(
    template: Template.Text,
    modifier: Modifier = Modifier,
    parentTemplateId: String? = null,
    row: Int = 0,
    iteration: Int = 0,
    onCard: Boolean = false,
) {
    val context = LocalContext.current
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val cache = LocalStateCache.current
    val state = remember(row, template, parentTemplateId, iteration) {
        TextFieldState(
            initialText = cache.getText(
                parentTemplateId = parentTemplateId,
                templateId = template.id,
                row = row,
                iteration = iteration
            )
        )
    }

    suspend fun save(value: String) {
        withContext(NonCancellable) {
            cache.putText(
                parentTemplateId = parentTemplateId,
                templateId = template.id,
                row = row,
                iteration = iteration,
                value = value
            )
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { state.text.toString() }.collectLatest {
            if (template.required) {
                if (it.isBlank()) {
                    errorMsg = context.getString(R.string.error_emptyValue)
                    save(it.trim())
                    return@collectLatest
                } else {
                    errorMsg = null
                }
            }

            template.regex?.let { exp ->
                if (it.isBlank() && !template.required) return@collectLatest
                if (!exp.toRegex().matches(it)) {
                    errorMsg = template.errorMsg ?: context.getString(R.string.error_invalidValue)
                } else if (errorMsg != null) {
                    errorMsg = null
                }
            }
            save(it.trim())
        }
    }

    val theModifier = modifier
        .widthIn(max = 350.dp)

    when (val kind = template.kind) {
        Kind.Bool -> {
            AppCheckbox(
                checked = state.text.toString().toBooleanStrictOrNull() ?: false,
                onCheckedChange = { checked ->
                    state.setTextAndPlaceCursorAtEnd(checked.toString())
                },
                text = template.name,
                modifier = theModifier
            )
        }
        is Kind.Date -> {
            val coroutineScope = rememberCoroutineScope()
            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                focusManager.clearFocus()
            }

            DateTextField(
                state = state,
                errorMsg = errorMsg,
                format = kind.format,
                label = template.name,
                onCard = onCard,
                onValueChange = {
                    coroutineScope.launch {
                        save(state.text.toString())
                    }
                },
                placeholder = template.placeholder,
                modifier = theModifier
            )
        }
        is Kind.Float -> {
            AppTextField(
                state = state,
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
                digitPlaceholder = true,
                onCard = onCard,
                modifier = theModifier
            )
        }
        is Kind.Int -> {
            AppTextField(
                state = state,
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
            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                focusManager.clearFocus()
                state.setTextAndPlaceCursorAtEnd(
                    cache.getText(
                        parentTemplateId = parentTemplateId,
                        templateId = template.id,
                        row = row,
                        iteration = iteration
                    )
                )
            }

            ReferenceTextField(
                state = state,
                label = template.name,
                errorMsg = errorMsg,
                placeholder = stringResource(R.string.placeholder_reference),
                onClick = {
                    Navigator.navigate(
                        Screen.HandbookDialog(
                            handbookId = kind.handbookId,
                            draftId = cache.draftId,
                            parentTemplateId = parentTemplateId,
                            templateId = template.id,
                            row = row,
                            iteration = iteration,
                        )
                    )
                },
                onCard = onCard,
                modifier = theModifier
            )
        }
        is Kind.String -> {
            AppTextField(
                state = state,
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                error = errorMsg,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                label = template.name,
                placeholder = stringResource(R.string.placeholder_input),
                lineLimits = if (kind.maxLines > 1) {
                    TextFieldLineLimits.MultiLine(
                        maxHeightInLines = kind.maxLines,
                        minHeightInLines = kind.maxLines.coerceAtMost(6)
                    )
                } else TextFieldLineLimits.SingleLine,
                onCard = onCard,
                modifier = theModifier
            )
        }
    }
}