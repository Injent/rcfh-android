package ru.rcfh.glpm.feature.form.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import ru.rcfh.core.sdui.state.CalculatedState
import ru.rcfh.core.sdui.state.RepeatableState
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.component.AppCard
import ru.rcfh.designsystem.component.AppSmallButton
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.glpm.feature.form.R

@Composable
fun RepeatableView(
    state: RepeatableState,
    horizontalPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        MediumContent(
            state = state,
            horizontalPadding = horizontalPadding,
            modifier = modifier
        )
    } else {
        CompactContent(
            state = state,
            modifier = modifier
        )
    }
}

@Composable
private fun MediumContent(
    state: RepeatableState,
    horizontalPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var maxCardHeight by remember { mutableStateOf(0.dp) }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
        contentPadding = PaddingValues(
            start = horizontalPadding.calculateStartPadding(LayoutDirection.Ltr) + AppTheme.spacing.l,
            end = horizontalPadding.calculateEndPadding(LayoutDirection.Ltr) + AppTheme.spacing.l,
            top = AppTheme.spacing.l,
            bottom = AppTheme.spacing.xs
        ),
        modifier = modifier
    ) {
        itemsIndexed(
            items = state.groups,
        ) { groupIndex, group ->
            var currentHeight by remember { mutableStateOf(0.dp) }

            AppCard(
                contentPadding = PaddingValues(AppTheme.spacing.l),
                modifier = Modifier
                    .widthIn(min = 380.dp)
                    .animateItem()
                    .width(IntrinsicSize.Max)
                    .onSizeChanged { size ->
                        density.run {
                            val height = size.height.toDp()
                            currentHeight = height
                            if (height > maxCardHeight) {
                                maxCardHeight = height
                            }
                        }
                    }
            ) {
                Text(
                    text = state.name,
                    style = AppTheme.typography.headline1,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier
                )
                ControlHeader(
                    groupIndex = groupIndex,
                    onRemove = { state.removeGroup(groupIndex) }
                )
                group.forEachIndexed { index, fieldState ->
                    val theModifier = Modifier
                        .fillMaxWidth()
                        .thenIf(index > 0) {
                            padding(top = AppTheme.spacing.s)
                        }
                    when (fieldState) {
                        is CalculatedState -> CalculatedView(
                            state = fieldState,
                            hasLine = false,
                            modifier = theModifier
                        )
                        is TextState -> {
                            TextFieldView(
                                state = fieldState,
                                onCard = true,
                                modifier = theModifier
                            )
                        }
                        else -> Unit
                    }
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = if (state.groups.isNotEmpty()) {
                    Alignment.CenterHorizontally
                } else {
                    Alignment.Start
                },
                modifier = Modifier
                    .thenIf(state.groups.isNotEmpty()) {
                        height(maxCardHeight)
                            .padding(horizontal = AppTheme.spacing.xxl)
                    }
            ) {
                if (state.groups.isEmpty()) {
                    Text(
                        text = state.name,
                        style = AppTheme.typography.headline1,
                        color = AppTheme.colorScheme.foreground1,
                        modifier = Modifier
                            .padding(bottom = AppTheme.spacing.s)
                    )
                }
                AppSmallButton(
                    text = stringResource(R.string.button_add),
                    onClick = state::addGroup,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun CompactContent(
    state: RepeatableState,
    modifier: Modifier = Modifier
) {
    AppCard(
        contentPadding = PaddingValues(
            top = AppTheme.spacing.l,
            start = AppTheme.spacing.l,
            end = AppTheme.spacing.l,
            bottom = AppTheme.spacing.sNudge
        ),
        modifier = modifier
    ) {
        Text(
            text = state.name,
            style = AppTheme.typography.headline1,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
        )

        state.groups.forEachIndexed { groupIndex, group ->
            ControlHeader(
                groupIndex = groupIndex,
                onRemove = { state.removeGroup(groupIndex) }
            )
            group.forEachIndexed { index, fieldState ->
                val theModifier = Modifier
                    .fillMaxWidth()
                    .thenIf(index > 0) {
                        padding(top = AppTheme.spacing.s)
                    }
                when (fieldState) {
                    is CalculatedState -> CalculatedView(
                        state = fieldState,
                        hasLine = false,
                        modifier = theModifier
                    )
                    is TextState -> {
                        TextFieldView(
                            state = fieldState,
                            onCard = true,
                            modifier = theModifier
                        )
                    }
                    else -> Unit
                }
            }
        }

        HorizontalDivider(
            color = AppTheme.colorScheme.stroke2,
            thickness = 1.2.dp,
            modifier = Modifier
                .padding(top = AppTheme.spacing.l)
        )
        AppTextButton(
            text = stringResource(R.string.button_add),
            onClick = state::addGroup,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.spacing.sNudge)
        )
    }
}

@Composable
private fun ControlHeader(
    groupIndex: Int,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            color = AppTheme.colorScheme.stroke2,
            thickness = 1.2.dp,
            modifier = Modifier
                .padding(top = AppTheme.spacing.l)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Запись ${groupIndex + 1}",
                style = AppTheme.typography.subheadline,
                color = AppTheme.colorScheme.foreground3,
                modifier = Modifier
                    .padding(top = AppTheme.spacing.s)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.button_delete),
                color = AppTheme.colorScheme.foregroundError,
                style = AppTheme.typography.subheadlineButton,
                modifier = Modifier
                    .clickable {
                        onRemove()
                    }
                    .padding(top = AppTheme.spacing.s, bottom = AppTheme.spacing.l)
            )
        }
    }
}