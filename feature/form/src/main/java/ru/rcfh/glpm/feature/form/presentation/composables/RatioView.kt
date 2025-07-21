package ru.rcfh.glpm.feature.form.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.state.RatioState
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.icon.Add
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Minus
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf

@Composable
fun RatioView(
    state: RatioState,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        val sumOfRatios by derivedStateOf {
            state.values
                .drop(1)
                .sumOf { textState ->
                    textState.value.toIntOrNull() ?: 0
                }
        }

        snapshotFlow { sumOfRatios }.collect { sum ->
            state.values[0].value = (100 - sum).toString()
        }
    }

    Row(
        modifier = modifier
    ) {
        state.values.getOrNull(0)?.let {
            CalculatedRatio(
                state = it,
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
            contentPadding = PaddingValues(
                start = AppTheme.spacing.s,
                end = AppTheme.spacing.l
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(
                items = state.values
            ) { index, ratio ->
                // First column is calculated. No need to input values manually
                if (index == 0) return@itemsIndexed
                RatioViewItem(
                    state = ratio,
                    maxLimitReached = state.maxLimitReached,
                    minLimitReached = state.minLimitReached
                )
            }
        }
    }
}

@Composable
private fun CalculatedRatio(
    state: TextState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(48.dp)
            .background(AppTheme.colorScheme.background2)
    ) {
        Text(
            text = state.label.substringBefore('\n'),
            style = AppTheme.typography.subheadline,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = AppTheme.spacing.s)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AppTheme.colorScheme.background1,
                    shape = AppTheme.shapes.default
                )
        ) {
            Spacer(Modifier.height(48.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
            ) {
                Text(
                    text = state.value,
                    style = AppTheme.typography.headline1,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier
                )
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun RatioViewItem(
    state: TextState,
    maxLimitReached: Boolean,
    minLimitReached: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .widthIn(min = 48.dp)
            .width(IntrinsicSize.Max)
            .alpha(if (state.enabled) 1f else .5f)
    ) {
        Text(
            text = state.label.substringBefore('\n'),
            style = AppTheme.typography.subheadline,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = AppTheme.spacing.s)
        )
        when (state.visual) {
            is Visual.Decimal -> {
                AppAddButton(
                    onClick = {
                        if (!state.enabled) return@AppAddButton

                        state.value = ((state.value.toIntOrNull() ?: 0) + 1)
                            .coerceAtMost(100).toString()
                    },
                    enabled = (state.value.toIntOrNull() ?: 0) < 100 && !maxLimitReached,
                    icon = AppIcons.Add,
                    shape = AppTheme.shapes.defaultTopCarved,
                    modifier = Modifier
                        .widthIn(max = 50.dp)
                )
                DecimalTextField(
                    state = state,
                    modifier = Modifier
                        .widthIn(max = 50.dp)
                        .height(48.dp)
                )
                AppAddButton(
                    onClick = {
                        if (!state.enabled) return@AppAddButton

                        state.value = ((state.value.toIntOrNull() ?: 0) - 1)
                            .coerceAtMost(100).toString()
                    },
                    enabled = (state.value.toIntOrNull() ?: 0) > 0 && !minLimitReached,
                    icon = AppIcons.Minus,
                    shape = AppTheme.shapes.defaultBottomCarved,
                    modifier = Modifier
                        .widthIn(max = 50.dp)
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun DecimalTextField(
    state: TextState,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = state.value,
        onValueChange = {},
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        readOnly = true,
        textStyle = AppTheme.typography.body.copy(textAlign = TextAlign.Center),
        modifier = modifier,
        keyboardActions = KeyboardActions {
            focusManager.moveFocus(FocusDirection.Next)
        },
        decorationBox = { textField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colorScheme.background4)
                    .thenIf(state.error != null) {
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

@Composable
fun AppAddButton(
    onClick: () -> Unit,
    icon: ImageVector,
    enabled: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (!isPressed) {
            currentCoroutineContext().cancel()
        }
        var step = 300L
        while (isPressed) {
            onClick()
            step = (step - 50).coerceAtLeast(50)
            delay(step)
        }
    }

    AppIconButton(
        icon = AppIcon(
            icon = icon,
            onClick = onClick,
            tint = AppTheme.colorScheme.foregroundOnBrand
        ),
        enabled = enabled,
        interactionSource = interactionSource,
        containerColor = AppTheme.colorScheme.backgroundBrand,
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    )
}