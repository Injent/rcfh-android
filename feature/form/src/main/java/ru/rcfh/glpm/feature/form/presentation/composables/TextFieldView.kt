package ru.rcfh.glpm.feature.form.presentation.composables

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.event.SetReference
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.component.AppCheckbox
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.component.DateTextField
import ru.rcfh.designsystem.component.ReferenceTextField
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.glpm.feature.form.R
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen
import java.util.UUID

@Composable
fun TextFieldView(
    state: TextState,
    modifier: Modifier = Modifier,
    onCard: Boolean = false,
) {
    val focusManager = LocalFocusManager.current

    val theModifier = modifier
        .widthIn(max = 350.dp)
        .alpha(
            if (state.enabled) 1f else 0.5f
        )

    when (val visual = state.visual) {
        Visual.Checkbox -> {
            AppCheckbox(
                checked = state.value.toBooleanStrictOrNull() ?: false,
                onCheckedChange = { checked ->
                    state.value = checked.toString()
                },
                enabled = state.enabled,
                text = state.label,
                modifier = theModifier
            )
        }
        is Visual.Date -> {
            DateTextField(
                value = state.value,
                onValueChange = { state.value = it },
                format = visual.format,
                label = state.label,
                onCard = onCard,
                placeholder = remember(visual) {
                    visual.format
                        .replace('y', 'Г')
                        .replace('d', 'Д')
                },
                readOnly = !state.enabled,
                error = if (state.enabled) state.error else null,
                modifier = theModifier
            )
        }
        is Visual.Reference -> {
            val scope = rememberCoroutineScope()
            val callbackId = rememberSaveable(key = state.id) { UUID.randomUUID().toString() }
            LaunchedEffect(callbackId) {
                state.document.observeEvent(SetReference::class) { event ->
                    if (event.callbackId == callbackId) {
                        state.value = event.value
                    }
                }
            }
            ReferenceTextField(
                value = state.value,
                label = state.label,
                error = if (state.enabled) state.error else null,
                placeholder = stringResource(R.string.placeholder_reference),
                onClick = {
                    if (!state.enabled) return@ReferenceTextField
                    scope.launch {
                        Navigator.navigate(
                            Screen.HandbookSearch(
                                documentId = state.document.documentId,
                                handbookId = visual.handbookId,
                                callbackId = callbackId,
                                title = state.label,
                                selectedOption = state.value.takeIf(String::isNotEmpty)
                            )
                        )
                    }
                },
                onCard = onCard,
                modifier = theModifier
            )
        }
        is Visual.Text,
        is Visual.Number,
        is Visual.Decimal -> {
            AppTextField(
                value = state.value,
                onValueChange = {
                    state.value = it
                },
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = when (visual) {
                        is Visual.Decimal -> KeyboardType.Decimal
                        is Visual.Number -> KeyboardType.Number
                        else -> KeyboardType.Text
                    },
                    imeAction = ImeAction.Next
                ),
                label = state.label,
                placeholder = stringResource(
                    when (visual) {
                        is Visual.Decimal -> R.string.placeholder_int
                        is Visual.Number -> R.string.placeholder_float
                        else -> R.string.placeholder_input
                    }
                ),
                lineLimits = if (visual is Visual.Text && visual.multiline) {
                    TextFieldLineLimits.MultiLine(minHeightInLines = 6)
                } else TextFieldLineLimits.SingleLine,
                error = if (state.enabled) state.error else null,
                trailingIcon = {
                    val unit = when (visual) {
                        is Visual.Decimal -> visual.unit
                        is Visual.Number -> visual.unit
                        else -> null
                    }
                    unit?.let {
                        Text(
                            text = it,
                            style = AppTheme.typography.callout
                        )
                    }
                },
                readOnly = !state.enabled,
                onCard = false,
                modifier = theModifier
            )
        }
    }
}
