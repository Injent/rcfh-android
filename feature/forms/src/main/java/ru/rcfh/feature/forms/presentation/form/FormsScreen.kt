package ru.rcfh.feature.forms.presentation.form

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.compose.ProvideStateStorage
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.designsystem.component.AppLargeButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.forms.component.RepeatableInput
import ru.rcfh.feature.forms.component.TableInput
import ru.rcfh.feature.forms.component.TextInput
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.formsScreen() {
    composable<Screen.Forms> {
        FormsRoute()
    }
}

@Composable
private fun FormsRoute() {
    val viewModel = koinViewModel<FormsViewModel>()
    LaunchedEffect(Unit) {
        viewModel.load()
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProvideStateStorage(
        cacheStorage = viewModel.cacheStorage,
        draftId = viewModel.draftId
    ) {
        FormsScreen(
            onEditRow = viewModel::onEditRow,
            onEditSection = viewModel::onEditSection,
            uiState = uiState,
        )
    }
}

@Composable
private fun FormsScreen(
    onEditRow: (tableId: String, row: Int) -> Unit,
    onEditSection: (templateId: String) -> Unit,
    uiState: FormsUiState,
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val startGradientColor = AppTheme.colorScheme.background2
    val statusBarHeightDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier
            .drawWithCache {
                val gradientHeight = statusBarHeightDp.toPx() + 4.dp.toPx()
                val brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.8f to startGradientColor,
                        1f to Color.Transparent
                    ),
                    endY = gradientHeight
                )

                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = brush,
                        size = Size(
                            width = size.width,
                            height = gradientHeight
                        )
                    )
                }
            }
    ) { innerPadding ->
        val theModifier = Modifier
            .padding(horizontal = AppTheme.spacing.l)
        val lazyListState = rememberLazyListState()

        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(
                start = WindowInsets.displayCutout.asPaddingValues()
                    .calculateStartPadding(LayoutDirection.Ltr)
                        + WindowInsets.navigationBars.asPaddingValues()
                    .calculateStartPadding(LayoutDirection.Ltr),
                end = WindowInsets.displayCutout.asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr)
                        + WindowInsets.navigationBars.asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr),
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                        + AppTheme.spacing.l,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        + AppTheme.spacing.l
            ),
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .focusGroup()
        ) {
            uiState.groupedTemplates.forEach { (group, templates) ->
                item {
                    Column(
                        modifier = theModifier
                            .padding(top = AppTheme.spacing.l)
                    ) {
                        group.takeIf { it.name.isNotEmpty() }?.let {
                            Text(
                                text = group.name,
                                style = AppTheme.typography.title3,
                                color = AppTheme.colorScheme.foreground1,
                                modifier = Modifier
                                    .padding(bottom = AppTheme.spacing.l)
                            )
                        }
                        HorizontalDivider(
                            color = AppTheme.colorScheme.stroke2,
                        )
                    }
                }
                items(
                    items = templates,
                    key = { temp -> temp.id }
                ) { template ->
                    when (template) {
                        is Template.ComparisonTable -> {
                            Button(
                                onClick = { onEditSection(template.id) }
                            ) {
                                Text(
                                    text = "Тест"
                                )
                            }
                        }
                        is Template.Repeatable -> {
                            RepeatableInput(
                                template = template,
                                modifier = Modifier
                                    .padding(top = AppTheme.spacing.l)
                                    .then(
                                        if (windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                                            Modifier
                                        } else {
                                            Modifier.fillMaxWidth()
                                        }
                                    )

                            )
                        }
                        is Template.Table -> {
                            TableInput(
                                template = template,
                                onEditRequest = { row ->
                                    onEditRow(template.id, row)
                                },
                                modifier = theModifier
                                    .padding(top = AppTheme.spacing.l)
                            )
                        }
                        is Template.Text -> {
                            TextInput(
                                template = template,
                                modifier = theModifier
                                    .fillMaxWidth()
                                    .padding(top = AppTheme.spacing.l)
                            )
                        }
                    }
                }
            }

            item {
                AppLargeButton(
                    text = "Сохранить",
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp)
                        .padding(AppTheme.spacing.l)
                )
            }
        }
    }
}

