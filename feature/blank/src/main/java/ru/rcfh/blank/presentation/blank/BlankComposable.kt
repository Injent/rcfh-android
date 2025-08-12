package ru.rcfh.blank.presentation.blank

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.rcfh.blank.compose.bottom
import ru.rcfh.blank.compose.end
import ru.rcfh.blank.compose.start
import ru.rcfh.blank.compose.top
import ru.rcfh.blank.presentation.viewer.ViewerUiState
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.component.Component
import ru.rcfh.blank.ui.component.RepeatableComponent
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.core.model.FormTab
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.screenRotation
import ru.rcfh.glpm.ui.getDisplayName

@Composable
internal fun BlankComposable(
    viewerUiState: ViewerUiState,
    innerPadding: PaddingValues,
    navigator: ViewerNavigator,
) {
    when (viewerUiState) {
        is ViewerUiState.Error -> {
            BlankComposableError()
        }
        is ViewerUiState.Loading -> {
            BlankComposableLoading()
        }
        is ViewerUiState.Success -> {
            BlankComposableContent(
                documentScope = viewerUiState.document.documentScope,
                state = viewerUiState.document.state,
                components = viewerUiState.document.components[viewerUiState.selectedTab.formId] ?: persistentListOf(),
                innerPadding = innerPadding,
                navigator = navigator,
                formTab = viewerUiState.selectedTab
            )
        }
    }
}

@Composable
private fun BlankComposableContent(
    documentScope: DocumentScope,
    state: Element,
    components: ImmutableList<Component>,
    innerPadding: PaddingValues,
    formTab: FormTab.Tab,
    navigator: ViewerNavigator,
) {
    val context = LocalContext.current
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val screenRotation = screenRotation()

    val columns = when {
        windowSize.isWidthAtLeastBreakpoint(1000) -> 3
        windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> 2
        else -> 1
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(
            top = 16.dp + innerPadding.top,
            start = innerPadding.start,
            end = innerPadding.end,
            bottom = 16.dp + innerPadding.bottom
        ),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
        modifier = Modifier
            .fillMaxSize()
            .focusGroup()
    ) {
        item(
            span = { GridItemSpan(columns) }
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = AppTheme.spacing.l)
            ) {
                AppBackButton(
                    onClick = navigator::goBack
                )
                Text(
                    text = formTab.getDisplayName(context),
                    style = AppTheme.typography.title3,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier.padding(start = AppTheme.spacing.m)
                )
            }
        }
        items(
            items = components,
            span = { component ->
                when (component) {
                    is RepeatableComponent -> columns
                    else -> 1
                }.let { GridItemSpan(it) }
            }
        ) { component ->
            @Composable
            fun ComponentContent() {
                component.Content(
                    state = state,
                    navigator = navigator,
                    documentScope = documentScope
                )
            }

            if (component is RepeatableComponent) {
                ComponentContent()
            } else {
                Box(
                    modifier = Modifier
                        .padding(horizontal = AppTheme.spacing.l)
                ) {
                    ComponentContent()
                }
            }
        }
    }
}