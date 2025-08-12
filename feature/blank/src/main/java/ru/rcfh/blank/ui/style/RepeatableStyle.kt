package ru.rcfh.blank.ui.style

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.queryapi.query
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.state.ArrayElement
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.blank.ui.state.ObjectElement
import ru.rcfh.blank.ui.state.create
import ru.rcfh.designsystem.component.AppCard
import ru.rcfh.designsystem.component.AppSmallButton
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.glpm.feature.blank.R

@Composable
fun RepeatableStyle(
    documentScope: DocumentScope,
    state: Element,
    rootPath: String,
    maxEntries: Int,
    enabled: Boolean,
    name: String,
    navigator: ViewerNavigator,
    modifier: Modifier = Modifier,
    horizontalPadding: PaddingValues = PaddingValues()
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass

    fun onAdd() {
        if (!enabled) return
        state.queryOrCreate(rootPath, state.create<ArrayElement>()).array?.add(state.create<ObjectElement>())
    }
    fun onRemove(index: Int) {
        if (!enabled) return
        state.query(rootPath)?.array?.removeAt(index)
    }

    if (windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        MediumContent(
            documentScope = documentScope,
            state = state,
            rootPath = rootPath,
            maxEntries = maxEntries,
            enabled = enabled,
            name = name,
            navigator = navigator,
            onAdd = ::onAdd,
            onRemove = ::onRemove,
            modifier = modifier
        )
    } else {
        CompactContent(
            documentScope = documentScope,
            state = state,
            rootPath = rootPath,
            maxEntries = maxEntries,
            enabled = enabled,
            name = name,
            navigator = navigator,
            onAdd = ::onAdd,
            onRemove = ::onRemove,
            modifier = modifier
        )
    }
}

@Composable
private fun CompactContent(
    documentScope: DocumentScope,
    state: Element,
    rootPath: String,
    maxEntries: Int,
    enabled: Boolean,
    name: String,
    navigator: ViewerNavigator,
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
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Text(
            text = name,
            style = AppTheme.typography.headline1,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
        )

        val groups = documentScope.repeatableGroups(rootPath)

        groups.forEachIndexed { groupIndex, group ->
            ControlHeader(
                groupIndex = groupIndex,
                onRemove = { onRemove(groupIndex) }
            )
            group.components.forEach { component ->
                component.Content(
                    state = state,
                    navigator = navigator,
                    documentScope = documentScope
                )
                Spacer(Modifier.height(AppTheme.spacing.l))
            }
        }

        if (enabled && groups.size < maxEntries) {
            HorizontalDivider(
                color = AppTheme.colorScheme.stroke2,
                thickness = 1.2.dp,
                modifier = Modifier
                    .padding(top = AppTheme.spacing.l)
            )

            AppTextButton(
                text = stringResource(R.string.feature_blank_button_add),
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.spacing.sNudge)
            )
        }
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
                text = stringResource(R.string.feature_blank_info_entry, groupIndex + 1),
                style = AppTheme.typography.subheadline,
                color = AppTheme.colorScheme.foreground3,
                modifier = Modifier
                    .padding(top = AppTheme.spacing.s)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.feature_blank_button_delete),
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

@Composable
private fun MediumContent(
    documentScope: DocumentScope,
    state: Element,
    rootPath: String,
    maxEntries: Int,
    enabled: Boolean,
    name: String,
    navigator: ViewerNavigator,
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
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        val groups = documentScope.repeatableGroups(rootPath)

        itemsIndexed(
            items = groups,
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
                    text = name,
                    style = AppTheme.typography.headline1,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier
                )
                ControlHeader(
                    groupIndex = groupIndex,
                    onRemove = { onRemove(groupIndex) }
                )
                group.components.forEach { component ->
                    component.Content(
                        state = state,
                        navigator = navigator,
                        documentScope = documentScope
                    )
                    Spacer(Modifier.height(AppTheme.spacing.l))
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = if (groups.isNotEmpty()) {
                    Alignment.CenterHorizontally
                } else {
                    Alignment.Start
                },
                modifier = Modifier
                    .then(
                        if (groups.isNotEmpty()) {
                            Modifier
                                .height(maxCardHeight)
                                .padding(horizontal = AppTheme.spacing.xxl)
                        } else Modifier
                    )
            ) {
                if (groups.isEmpty()) {
                    Text(
                        text = name,
                        style = AppTheme.typography.headline1,
                        color = AppTheme.colorScheme.foreground1,
                        modifier = Modifier
                            .padding(bottom = AppTheme.spacing.s)
                    )
                }
                if (enabled && groups.size < maxEntries) {
                    AppSmallButton(
                        text = stringResource(R.string.feature_blank_button_add),
                        onClick = onAdd,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}