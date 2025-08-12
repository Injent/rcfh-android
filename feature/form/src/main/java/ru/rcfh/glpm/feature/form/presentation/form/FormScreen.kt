package ru.rcfh.glpm.feature.form.presentation.form

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.rcfh.core.sdui.state.CalculatedState
import ru.rcfh.core.sdui.state.ComparisonTableState
import ru.rcfh.core.sdui.state.LinkedState
import ru.rcfh.core.sdui.state.LocationState
import ru.rcfh.core.sdui.state.RatioState
import ru.rcfh.core.sdui.state.RepeatableState
import ru.rcfh.core.sdui.state.Table4State
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.core.sdui.template.FormTab
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.component.AppItemCard
import ru.rcfh.designsystem.component.AppSmallButton
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.component.SnackbarController
import ru.rcfh.designsystem.component.SnackbarMessage
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Attention
import ru.rcfh.designsystem.icon.Table
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ListenEvents
import ru.rcfh.designsystem.util.ScreenRotation
import ru.rcfh.designsystem.util.screenRotation
import ru.rcfh.glpm.feature.form.R
import ru.rcfh.glpm.feature.form.presentation.composables.LocationView
import ru.rcfh.glpm.feature.form.presentation.composables.RatioView
import ru.rcfh.glpm.feature.form.presentation.composables.RepeatableView
import ru.rcfh.glpm.feature.form.presentation.composables.TextFieldView

@Composable
internal fun FormRoute(
    documentId: Int,
    formId: Int,
    optionsId: String?,
    topPadding: Dp,
) {
    val context = LocalContext.current
    val viewModel = koinViewModel<FormViewModel>(key = "$documentId/$formId") {
        parametersOf(FormKoinParams(documentId, formId, optionsId))
    }
    ListenEvents(viewModel.events) { event ->
        when (event) {
            is FormEvent.ShowToast -> SnackbarController.show(
                SnackbarMessage(
                    text = context.getString(event.messageResId),
                    icon = AppIcons.Attention,
                    tint = Color.White,
                    displayTimeSeconds = 3
                )
            )
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.back()
    }

    when (uiState) {
        is FormUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = AppTheme.colorScheme.foreground,
                )
            }
        }
        is FormUiState.Success -> {
            FormScreen(
                uiState = (uiState as FormUiState.Success),
                openTable = viewModel::openTable,
                onSetOption = viewModel::onSetOption,
                openComparisonTable = viewModel::openComparisonTable,
                onBack = viewModel::back,
                topPadding = topPadding
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun FormScreen(
    openTable: (String) -> Unit,
    openComparisonTable: (String) -> Unit,
    onSetOption: (Int) -> Unit,
    uiState: FormUiState.Success,
    onBack: () -> Unit,
    topPadding: Dp
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val screenRotation = screenRotation()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier
            .fillMaxSize()
    ) {
        val theModifier = Modifier
            .padding(top = AppTheme.spacing.l)

        val columns = when {
            windowSize.isWidthAtLeastBreakpoint(1000) -> 3
            windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> 2
            else -> 1
        }
        val endPadding = when (screenRotation) {
            ScreenRotation.LEFT -> WindowInsets.navigationBars.getEnd()
            ScreenRotation.NONE -> 0.dp
            ScreenRotation.RIGHT -> WindowInsets.displayCutout.getEnd()
        }
        val lazyGridState = rememberLazyGridState()
        val isReadyToShow by remember {
            derivedStateOf {
                lazyGridState.layoutInfo.visibleItemsInfo.isNotEmpty()
            }
        }
        val alpha by animateFloatAsState(
            targetValue = if (isReadyToShow) 1f else 0f,
            animationSpec = tween(delayMillis = 50)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(
                top = AppTheme.spacing.l + topPadding + WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                start = AppTheme.spacing.l,
                end = endPadding + AppTheme.spacing.l,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        + AppTheme.spacing.l
            ),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
            state = lazyGridState,
            modifier = Modifier
                .alpha(alpha)
                .fillMaxSize()
                .imePadding()
                .focusGroup()
        ) {
            item(span = { GridItemSpan(columns) }) {
                FormSelectableItems(
                    onSetOption = onSetOption,
                    uiState = uiState
                )
            }
            item(span = { GridItemSpan(columns) }) {
                Row(
                    modifier = Modifier
                        .padding(top = AppTheme.spacing.s)
                ) {
                    AppBackButton(
                        onClick = onBack
                    )
                    Text(
                        text = uiState.formState.name,
                        style = AppTheme.typography.title3,
                        color = AppTheme.colorScheme.foreground1,
                        modifier = Modifier.padding(start = AppTheme.spacing.m)
                    )
                }
            }
            items(
                items = uiState.formState.fields,
                key = { it.id },
                span = {
                    when (it) {
                        is RatioState,
                        is RepeatableState -> columns
                        else -> 1
                    }.let(::GridItemSpan)
                }
            ) { field ->
                when (field) {
                    is LocationState -> {
                        LocationView(
                            state = field,
                            modifier = theModifier
                                .fillMaxWidth()
                        )
                    }
                    is RepeatableState -> {
                        RepeatableView(
                            state = field,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillWidthOfParent(AppTheme.spacing.l)
                                .padding(top = AppTheme.spacing.l)
                        )
                    }
                    is ComparisonTableState -> {
                        AppItemCard(
                            label = field.name,
                            onClick = { openComparisonTable(field.id) },
                            icon = AppIcons.Table,
                            modifier = theModifier
                        )
                    }
                    is Table4State -> {
                        AppItemCard(
                            label = field.name,
                            onClick = { openTable(field.id) },
                            icon = AppIcons.Table,
                            modifier = theModifier
                        )
                    }
                    is TableState -> {
                        AppItemCard(
                            label = field.name,
                            onClick = { openTable(field.id) },
                            icon = AppIcons.Table,
                            modifier = theModifier
                        )
                    }
                    is TextState -> {
                        TextFieldView(
                            state = field,
                            modifier = theModifier
                                .fillMaxWidth()
                        )
                    }
                    is RatioState -> {
                        RatioView(
                            state = field,
                            modifier = theModifier
                                .fillMaxWidth()
                        )
                    }
                    is CalculatedState -> {}
                    is LinkedState -> {}
                    else -> Unit
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormSelectableItems(
    onSetOption: (Int) -> Unit,
    uiState: FormUiState.Success,
    modifier: Modifier = Modifier
) {
    var targetForm by remember { mutableStateOf<FormTab?>(null) }

    targetForm?.let { tab ->
        BasicAlertDialog(
            onDismissRequest = { targetForm = null },
            modifier = modifier,
        ) {
            Surface(
                color = AppTheme.colorScheme.background1,
                shape = AppTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .padding(AppTheme.spacing.l)
                ) {
                    Text(
                        text = stringResource(R.string.info_selectForm, tab.name),
                        style = AppTheme.typography.headline1,
                        color = AppTheme.colorScheme.foreground1
                    )
                    Spacer(Modifier.height(AppTheme.spacing.l))
                    Text(
                        text = stringResource(R.string.info_allFormDataWillBeLost),
                        style = AppTheme.typography.callout,
                        color = AppTheme.colorScheme.foreground1
                    )
                    Spacer(Modifier.height(AppTheme.spacing.l))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        AppTextButton(
                            text = stringResource(R.string.button_cancel),
                            shape = CircleShape,
                            color = AppTheme.colorScheme.foreground,
                            onClick = {
                                targetForm = null
                            },
                        )
                        Spacer(Modifier.width(AppTheme.spacing.l))
                        AppSmallButton(
                            text = stringResource(R.string.button_yes),
                            onClick = {
                                targetForm = null
                                onSetOption(tab.formId)
                            },
                        )
                    }
                }
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        for (tab in uiState.formTabs) {
            val containerColor by animateColorAsState(
                if (tab.formId == uiState.formState.id) {
                    AppTheme.colorScheme.backgroundBrand
                } else AppTheme.colorScheme.background1
            )
            val contentColor by animateColorAsState(
                if (tab.formId == uiState.formState.id) {
                    AppTheme.colorScheme.foregroundOnBrand
                } else AppTheme.colorScheme.foreground1
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(AppTheme.shapes.default)
                    .clickable(enabled = tab.formId != uiState.formState.id) {
                        targetForm = tab
                    }
                    .background(containerColor)
            ) {
                Text(
                    text = tab.name,
                    style = AppTheme.typography.calloutButton,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun WindowInsets.getEnd(): Dp {
    return asPaddingValues().calculateEndPadding(LayoutDirection.Ltr)
}

fun Modifier.fillWidthOfParent(parentPadding: Dp): Modifier {
    return layout { measurable, constraints ->
        // This is to force layout to go beyond the borders of its parent
        val placeable = measurable.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth + 2 * parentPadding.roundToPx(),
            ),
        )
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}