package ru.rcfh.feature.forms.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rcfh.core.sdui.model.Kind
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.icon.Add
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Minus
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf

@Composable
fun UnaryFormComposable(
    state: TextState,
    onStateChange: (TextState) -> Unit,
    template: Template.Text,
    minLimitReached: Boolean,
    maxLimitReached: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
    ) {
        Text(
            text = template.name.substringBefore('\n'),
            style = AppTheme.typography.subheadline,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = AppTheme.spacing.s)
        )
        when (template.kind) {
            is Kind.Int -> {
                AppIconButton(
                    icon = AppIcon(
                        icon = AppIcons.Add,
                        onClick = {
                            state.copy(
                                value = ((state.value.toIntOrNull() ?: 0) + 1).coerceAtMost(100).toString()
                            ).also(onStateChange)
                        },
                        tint = AppTheme.colorScheme.foregroundOnBrand
                    ),
                    enabled = (state.value.toIntOrNull() ?: 0) < 100 && !maxLimitReached,
                    containerColor = AppTheme.colorScheme.backgroundBrand,
                    shape = AppTheme.shapes.defaultTopCarved,
                    modifier = Modifier
                        .size(48.dp)
                )
                DecimalTextField(
                    value = state.value,
                    onValueChange = {
                        state.copy(value = it).also(onStateChange)
                    },
                    modifier = Modifier
                        .height(48.dp)
                )
                AppIconButton(
                    icon = AppIcon(
                        icon = AppIcons.Minus,
                        onClick = {
                            state.copy(
                                value = ((state.value.toIntOrNull() ?: 0) - 1).coerceAtLeast(0).toString()
                            ).also(onStateChange)
                        },
                        tint = AppTheme.colorScheme.foregroundOnBrand
                    ),
                    enabled = (state.value.toIntOrNull() ?: 0) > 0 && !minLimitReached,
                    containerColor = AppTheme.colorScheme.backgroundBrand,
                    shape = AppTheme.shapes.defaultBottomCarved,
                    modifier = Modifier
                        .size(48.dp)
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun DecimalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val min = 0
    val max = 100
    val textFieldState = remember { TextFieldState(initialText = value) }
    var hasError by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (value != textFieldState.text.toString()) {
            hasError = false
            textFieldState.edit { replace(0, length, value) }
        }
    }

    LaunchedEffect(textFieldState.text) {
        val newText = textFieldState.text.toString()
        if (newText != value) {
            try {
                val a = newText.toInt()
                if (a > max) {
                    onValueChange(max.toString())
                } else if (a < min) {
                    onValueChange(min.toString())
                } else {
                    onValueChange(newText)
                }
                hasError = false
            } catch (_: NumberFormatException) {
                hasError = true
                onValueChange("")
            }
        }
    }
    BasicTextField(
        state = textFieldState,
        inputTransformation = {
            if (asCharSequence().length > max.toString().length) {
                revertAllChanges()
            }
            if (!asCharSequence().all { it.isDigit() }) {
                revertAllChanges()
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        textStyle = AppTheme.typography.body.copy(textAlign = TextAlign.Center),
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier,
        decorator = { textField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colorScheme.background4)
                    .thenIf(hasError) {
                        border(
                            width = 2.dp,
                            color = AppTheme.colorScheme.foregroundError
                        )
                    }
            ) {
                textField()
            }
        }
    )
}
