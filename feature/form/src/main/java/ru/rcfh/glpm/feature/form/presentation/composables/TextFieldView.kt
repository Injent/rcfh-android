package ru.rcfh.glpm.feature.form.presentation.composables

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.common.Format
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.event.SetReference
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.component.AppCheckbox
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.component.DateTextField
import ru.rcfh.designsystem.component.ReferenceTextField
import ru.rcfh.designsystem.icon.Add
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Cross
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
                        state.setReference(refDependency = event.refDependency, value = event.value)
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
                        val parentDependency = state.getParentDependency()
                        Navigator.navigate(
                            Screen.HandbookSearch(
                                documentId = state.document.documentId,
                                handbookId = visual.handbookId,
                                templateId = state.id,
                                rowIndex = state.rowIndex,
                                callbackId = callbackId,
                                title = state.label,
                                selectedOption = state.value.takeIf(String::isNotEmpty),
                                dependencyHandbook = parentDependency?.handbookId,
                                dependencyRefId = parentDependency?.refId,
                                shouldHaveFilledDependency = (state.visual as? Visual.Reference)?.dependsOn != null
                            )
                        )
                    }
                },
                enabled = state.enabled,
                onCard = onCard,
                modifier = theModifier
            )
        }
        is Visual.Text,
        is Visual.Number,
        is Visual.Decimal -> {
            when (state.format) {
                is Format.CadastralNumber -> {
                    val textState = rememberTextFieldState(state.value)

                    LaunchedEffect(Unit) {
                        snapshotFlow { textState.text }
                            .collect {
                                state.value = it.toString()
                            }
                    }

                    AppTextField(
                        state = textState,
                        onKeyboardAction = {
                            focusManager.moveFocus(FocusDirection.Next)
                        },
                        inputTransformation = {
                            if (length > 14 && originalText.length < length) {
                                revertAllChanges()
                            }
                        },
                        outputTransformation = {
                            if (length >= 2) insert(2, ":")
                            if (length >= 5) insert(5, ":")
                            if (length > 12) insert(12, ":")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = state.label,
                        placeholder = "00:00:0000000:0000",
                        lineLimits = TextFieldLineLimits.SingleLine,
                        error = if (state.enabled) state.error else null,
                        readOnly = !state.enabled,
                        onCard = onCard,
                        modifier = theModifier
                    )
                }
                else -> {
                    AppTextField(
                        value = state.value,
                        onValueChange = { newValue ->
                            state.value = newValue
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
                        button = {
                            if (visual is Visual.Decimal && visual.canSetPlus) {
                                AppIconButton(
                                    icon = AppIcon(
                                        icon = if (state.value == "+") AppIcons.Cross else AppIcons.Add,
                                        onClick = {
                                            state.value = if (state.value != "+") "+" else ""
                                        },
                                    )
                                )
                            }
                        },
                        readOnly = !state.enabled,
                        onCard = onCard,
                        modifier = theModifier
                    )
                }
            }
        }
    }
}