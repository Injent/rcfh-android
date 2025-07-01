package ru.rcfh.feature.forms.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.RepeatableState
import ru.rcfh.designsystem.component.AppCard
import ru.rcfh.designsystem.component.AppSmallButton
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.feature.forms.R

@Composable
fun RepeatableComposable(
    state: RepeatableState,
    template: Template.Repeatable,
    onStateChange: (RepeatableState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass

    fun onAdd() {
        val newGroup = List(template.templates.size) { "" }
        onStateChange(state.copy(groups = state.groups + listOf(newGroup)))
    }
    fun onRemove(groupIndex: Int) {
        val newGroups = state.groups.toMutableList()
        newGroups.removeAt(groupIndex)
        onStateChange(state.copy(groups = newGroups))
    }

    if (windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        MediumContent(
            state = state,
            template = template,
            onStateChange = onStateChange,
            onAdd = ::onAdd,
            onRemove = ::onRemove,
            modifier = modifier
        )
    } else {
        CompactContent(
            state = state,
            template = template,
            onStateChange = onStateChange,
            onAdd = ::onAdd,
            onRemove = ::onRemove,
            modifier = modifier
        )
    }
}

@Composable
private fun MediumContent(
    state: RepeatableState,
    template: Template.Repeatable,
    onStateChange: (RepeatableState) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var maxCardHeight by remember { mutableStateOf(0.dp) }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
        contentPadding = PaddingValues(
            start = AppTheme.spacing.l,
            end = AppTheme.spacing.l,
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
                contentPadding = PaddingValues(
                    top = AppTheme.spacing.l,
                    start = AppTheme.spacing.l,
                    end = AppTheme.spacing.l,
                    bottom = AppTheme.spacing.sNudge
                ),
                modifier = Modifier
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
                    text = template.name,
                    style = AppTheme.typography.headline1,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier
                )
                HorizontalDivider(
                    color = AppTheme.colorScheme.stroke2,
                    thickness = 1.2.dp,
                    modifier = Modifier
                        .padding(top = AppTheme.spacing.xl)
                )
                key(state.groups.size) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
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
                        Text(
                            text = stringResource(R.string.button_delete),
                            color = AppTheme.colorScheme.foregroundError,
                            style = AppTheme.typography.subheadlineButton,
                            modifier = Modifier
                                .clickable {
                                    onRemove(groupIndex)
                                }
                                .padding(top = AppTheme.spacing.s, bottom = AppTheme.spacing.l)
                        )
                    }

                    template.templates.forEachIndexed { templateIndex, template ->
                        val value = group[templateIndex]

                        TextComposable(
                            value = value,
                            onValueChange = { newValue ->
                                val newGroups = state.groups.toMutableList()
                                val newGroup = newGroups[groupIndex].toMutableList()
                                newGroup[templateIndex] = newValue
                                newGroups[groupIndex] = newGroup
                                onStateChange(state.copy(groups = newGroups))
                            },
                            template = template,
                            onCard = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = AppTheme.spacing.l)
                        )
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
                        text = template.name,
                        style = AppTheme.typography.headline1,
                        color = AppTheme.colorScheme.foreground1,
                        modifier = Modifier
                            .padding(bottom = AppTheme.spacing.s)
                    )
                }
                AppSmallButton(
                    text = stringResource(R.string.button_add),
                    onClick = onAdd,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun CompactContent(
    state: RepeatableState,
    template: Template.Repeatable,
    onStateChange: (RepeatableState) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
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
            text = template.name,
            style = AppTheme.typography.headline1,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
        )

        key(state.groups.size) {
            state.groups.forEachIndexed { groupIndex, group ->
                HorizontalDivider(
                    color = AppTheme.colorScheme.stroke2,
                    thickness = 1.2.dp,
                    modifier = Modifier
                        .padding(top = AppTheme.spacing.xl)
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
                                onRemove(groupIndex)
                            }
                            .padding(top = AppTheme.spacing.s, bottom = AppTheme.spacing.l)
                    )
                }
                template.templates.forEachIndexed { templateIndex, temp ->
                    val value = group[templateIndex]

                    TextComposable(
                        value = value,
                        onValueChange = { newValue ->
                            val newGroups = state.groups.toMutableList()
                            val newGroup = newGroups[groupIndex].toMutableList()
                            newGroup[templateIndex] = newValue
                            newGroups[groupIndex] = newGroup
                            onStateChange(state.copy(groups = newGroups))
                        },
                        template = temp,
                        onCard = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = AppTheme.spacing.l)
                    )
                }
            }
        }

        HorizontalDivider(
            color = AppTheme.colorScheme.stroke2,
            thickness = 1.2.dp,
            modifier = Modifier
                .padding(top = AppTheme.spacing.xs)
        )
        AppTextButton(
            text = stringResource(R.string.button_add),
            onClick = onAdd,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.spacing.sNudge)
        )
    }
}