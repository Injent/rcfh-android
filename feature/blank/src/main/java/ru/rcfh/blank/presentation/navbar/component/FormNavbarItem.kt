package ru.rcfh.blank.presentation.navbar.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import ru.rcfh.core.model.FormTab
import ru.rcfh.designsystem.component.AppCard
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.glpm.ui.getRomanNumber

@Composable
internal fun FormNavbarItem(
    tab: FormTab,
    onSelect: (id: Int) -> Unit,
    onReplace: (oldId: Int, newId: Int) -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val containerColor by animateColorAsState(
        if (selected) AppTheme.colorScheme.backgroundBrand
        else AppTheme.colorScheme.background2
    )
    val contentColor by animateColorAsState(
        if (selected) AppTheme.colorScheme.foregroundOnBrand
        else AppTheme.colorScheme.foreground1
    )
    val lineColor = AppTheme.colorScheme.foreground1
    val lineHorizontalPadding = density.run { AppTheme.spacing.l.toPx() }
    val lineYOffset = density.run { AppTheme.spacing.xs.toPx() }
    val strokeWidth = density.run { 3.dp.toPx() }
    var showPopup by rememberSaveable { mutableStateOf(false) }
    val isReplaceable = remember(tab) { FormTab.isReplaceable(tab.formId) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .drawWithContent {
                drawContent()
                if (tab is FormTab.ReplaceableTab) {
                    drawLine(
                        color = lineColor,
                        start = Offset(
                            x = lineHorizontalPadding,
                            y = size.height + lineYOffset
                        ),
                        end = Offset(
                            x = size.width - lineHorizontalPadding,
                            y = size.height + lineYOffset
                        ),
                        cap = StrokeCap.Round,
                        strokeWidth = strokeWidth
                    )
                }
            }
            .clip(AppTheme.shapes.default)
            .clickable {
                if (tab is FormTab.ReplaceableTab) {
                    showPopup = true
                } else {
                    onSelect(tab.formId)
                }
            }
            .background(containerColor)
    ) {
        Text(
            text = when (tab) {
                is FormTab.ReplaceableTab -> "?"
                is FormTab.Tab -> tab.getRomanNumber(context)
            },
            style = AppTheme.typography.subheadlineButton,
            color = contentColor,
        )

        if (showPopup && isReplaceable) {
            ReplaceableTabPopup(
                replaceableTab = tab,
                onDismissRequest = { showPopup = false },
                onReplace = onReplace,
            )
        }
    }
}

@Composable
private fun ReplaceableTabPopup(
    replaceableTab: FormTab,
    onDismissRequest: () -> Unit,
    onReplace: (oldId: Int, newId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    Popup(
        onDismissRequest = onDismissRequest,
        offset = IntOffset(
            x = 0,
            y = density.run { -AppTheme.spacing.s.roundToPx() }
        ),
    ) {
        AppCard(
            contentPadding = PaddingValues(AppTheme.spacing.s),
            shadowEnabled = true,
            modifier = modifier
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
            ) {
                @Composable
                fun TabItem(tab: FormTab.Tab) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(AppTheme.shapes.default)
                            .clickable {
                                onDismissRequest()
                                onReplace(replaceableTab.formId, tab.formId)
                            }
                            .background(AppTheme.colorScheme.background2)
                    ) {
                        Text(
                            text = tab.getRomanNumber(context),
                            style = AppTheme.typography.subheadlineButton,
                            color = AppTheme.colorScheme.foreground1,
                        )
                    }
                }

                when (replaceableTab) {
                    is FormTab.ReplaceableTab -> {
                        replaceableTab.tabs.forEach { TabItem(tab = it) }
                    }
                    is FormTab.Tab -> {
                        remember(replaceableTab) {
                            FormTab.findReplaceOptions(replaceableTab)
                        }.forEach { TabItem(tab = it) }
                    }
                }
            }
        }
    }
}