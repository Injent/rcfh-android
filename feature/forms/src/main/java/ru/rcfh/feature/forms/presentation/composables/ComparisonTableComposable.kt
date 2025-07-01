package ru.rcfh.feature.forms.presentation.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.ComparisonTableState
import ru.rcfh.designsystem.component.ContentCell
import ru.rcfh.designsystem.component.HeaderCell
import ru.rcfh.designsystem.theme.AppTheme

@Composable
internal fun ComparisonTable(
    state: ComparisonTableState,
    template: Template.ComparisonTable,
    onClick: (page: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass

    when {
        windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
                modifier = modifier.fillMaxWidth()
            ) {
                items(
                    count = state.groups.size.coerceAtLeast(1)
                ) { page ->
                    ComparisonTableContent(
                        state = state,
                        template = template,
                        page = page,
                        onClick = { onClick(page) }
                    )
                }
            }
        }
        else -> {
            val pagerState = rememberPagerState { state.groups.size.coerceAtLeast(1) }
            HorizontalPager(
                state = pagerState,
                pageSpacing = AppTheme.spacing.l,
                userScrollEnabled = state.groups.size > 1,
                modifier = modifier
            ) { page ->
                ComparisonTableContent(
                    state = state,
                    template = template,
                    page = page,
                    onClick = { onClick(page) }
                )
            }
        }
    }
}

@Composable
private fun ComparisonTableContent(
    state: ComparisonTableState,
    template: Template.ComparisonTable,
    page: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(IntrinsicSize.Max)
            .clip(AppTheme.shapes.default)
            .border(
                width = 1.dp,
                color = AppTheme.colorScheme.stroke1,
                shape = AppTheme.shapes.default
            )
    ) {
        HeaderCell(
            text = template.name,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = AppTheme.colorScheme.stroke1)
        )
        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            HeaderCell(
                text = template.firstSection.name,
                modifier = Modifier.weight(1f)
            )
            HeaderCell(
                text = template.secondSection.name,
                modifier = Modifier.weight(1f)
            )
        }
        val (firstSection, secondSection) = remember(page, state.groups) {
            state.groups.getOrNull(page) ?: Pair(listOf(), listOf())
        }

        repeat(template.firstSection.templates.size) { row ->
            Text(
                text = template.firstSection.templates[row].name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = AppTheme.colorScheme.stroke1)
                    .padding(vertical = AppTheme.spacing.xs)
            )
            Row(
                modifier = Modifier.height(IntrinsicSize.Max)
            ) {
                ContentCell(
                    text = firstSection.getOrNull(row)?.value ?: "",
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                )
                ContentCell(
                    text = secondSection.getOrNull(row)?.value ?: "",
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}