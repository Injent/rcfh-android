package ru.rcfh.feature.forms.presentation.form

import androidx.activity.compose.BackHandler
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.ComparisonTableState
import ru.rcfh.core.sdui.state.LocationState
import ru.rcfh.core.sdui.state.RepeatableState
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.component.AppItemCard
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Table
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.forms.presentation.composables.ComparisonTable
import ru.rcfh.feature.forms.presentation.composables.ConfirmExitDialog
import ru.rcfh.feature.forms.presentation.composables.LocationForm
import ru.rcfh.feature.forms.presentation.composables.RepeatableComposable
import ru.rcfh.feature.forms.presentation.composables.TextFormComposable
import ru.rcfh.feature.forms.state.LocalSavedStateHandle
import ru.rcfh.feature.forms.state.rememberFormStates
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen
import ru.rcfh.navigation.getRoute
import ru.rcfh.navigation.toRoute

fun NavGraphBuilder.formsScreen() {
    composable<Screen.Forms>(
        enterTransition = {
            when (initialState.destination.toRoute()) {
                getRoute<Screen.Documents>() -> slideInHorizontally { it }
                getRoute<Screen.Sheet>() -> slideInHorizontally() + scaleIn(initialScale = 0.85f)
                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.toRoute()) {
                getRoute<Screen.Documents>() -> slideOutHorizontally { it }
                getRoute<Screen.Sheet>() -> slideOutHorizontally() + scaleOut(targetScale = 0.85f)
                else -> null
            }
        }
    ) { backStackEntry ->
        val documentId = backStackEntry.toRoute<Screen.Forms>().documentId
        CompositionLocalProvider(
            LocalSavedStateHandle provides backStackEntry.savedStateHandle
        ) {
            FormsRoute(
                documentId = documentId,
            )
        }
    }
}

@Composable
private fun FormsRoute(
    documentId: Int,
) {
    val viewModel = koinViewModel<FormViewModel>(key = documentId.toString())

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is FormUiState.FormsUiState -> {
            FormsScreen(
                uiState = state,
                openSheets = viewModel::navigateToSheets,
                openSections = viewModel::navigateToSections,
                onBack = viewModel::back,
            )
        }
        is FormUiState.Loading -> Unit
    }
}

@Composable
private fun FormsScreen(
    uiState: FormUiState.FormsUiState,
    openSheets: (String) -> Unit,
    openSections: (String, Int) -> Unit,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val startGradientColor = AppTheme.colorScheme.background2
    val statusBarHeightDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val stateHolder = rememberFormStates(
        documentId = uiState.documentId,
        form = uiState.form,
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    if (showExitDialog) {
        ConfirmExitDialog(
            onConfirm = {
                showExitDialog = false
                onBack()
            },
            onDismissRequest = { showExitDialog = false }
        )
    }
    BackHandler(enabled = !showExitDialog) {
        showExitDialog = true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            ScrollableTabRow(
                selectedTabIndex = remember { uiState.formsTabs.indexOfFirst { it.formId == uiState.form.id } },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                uiState.formsTabs.forEach { tab ->
                    Tab(
                        selected = tab.formId == uiState.form.id,
                        onClick = {
                            scope.launch {
                                Navigator.navigate(
                                    Screen.Forms(
                                        documentId = uiState.documentId,
                                        formId = tab.formId
                                    )
                                ) {
                                    popUpTo<Screen.Documents>()
                                    launchSingleTop = true
                                }
                            }
                        },
                        text = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .background(
                                        color = if (tab.formId in uiState.invalidFormIds) ErrorColor else Color.Transparent,
                                        shape = CircleShape
                                    )
                            ) {
                                Text(
                                    text = tab.name.substringBefore('.'),
                                    style = AppTheme.typography.calloutButton,
                                )
                            }
                        },
                        selectedContentColor = AppTheme.colorScheme.foreground,
                        unselectedContentColor = AppTheme.colorScheme.foreground2,
                        modifier = Modifier
                            .height(48.dp)
                    )
                }
            }
        },
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

        val columns = when {
            windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> 3
            windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> 2
            else -> 1
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(
                start = WindowInsets.displayCutout.asPaddingValues()
                    .calculateStartPadding(LayoutDirection.Ltr)
                        + WindowInsets.navigationBars.asPaddingValues()
                    .calculateStartPadding(LayoutDirection.Ltr),
                end = WindowInsets.displayCutout.asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr)
                        + WindowInsets.navigationBars.asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr),
                top = AppTheme.spacing.l,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        + AppTheme.spacing.l
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .focusGroup()
        ) {
            uiState.form.name.let {
                item(
                    span = {
                        GridItemSpan(columns)
                    },
                ) {
                    Text(
                        text = it,
                        style = AppTheme.typography.title3,
                        color = AppTheme.colorScheme.foreground1,
                        modifier = Modifier
                            .padding(start = AppTheme.spacing.l)
                    )
                }
            }
            
            items(
                items = uiState.form.templates,
                key = { it.id },
                span = {
                    when (it) {
                        is Template.ComparisonTable,
                        is Template.Repeatable,
                        is Template.Table -> columns
                        else -> 1
                    }.let(::GridItemSpan)
                }
            ) { template ->
                when (template) {
                    is Template.Location -> {
                        LocationForm(
                            state = stateHolder[template.id] ?: LocationState(id = template.id),
                            onStateChange = {
                                stateHolder[template.id] = it
                            },
                            template = template,
                            modifier = theModifier
                                .fillMaxWidth()
                                .padding(top = AppTheme.spacing.l)
                        )
                    }
                    is Template.ComparisonTable -> {
                        Box {
                            ComparisonTable(
                                state = stateHolder[template.id] ?: ComparisonTableState(id = template.id),
                                template = template,
                                onClick = { page ->
                                    openSections(template.id, page)
                                },
                                modifier = theModifier
                                    .fillMaxWidth()
                                    .padding(top = AppTheme.spacing.l)
                            )
                        }
                    }
                    is Template.Text -> {
                        TextFormComposable(
                            state = stateHolder[template.id] ?: TextState(id = template.id, value = ""),
                            template = template,
                            onStateChange = {
                                stateHolder[template.id] = it
                            },
                            modifier = theModifier
                                .fillMaxWidth()
                                .padding(top = AppTheme.spacing.l)
                        )
                    }
                    is Template.Repeatable -> {
                        RepeatableComposable(
                            state = stateHolder[template.id] ?: RepeatableState(id = template.id),
                            template = template,
                            onStateChange = {
                                stateHolder[template.id] = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = AppTheme.spacing.l)
                        )
                    }
                    is Template.Table -> {
                        AppItemCard(
                            label = template.name,
                            onClick = { openSheets(template.id) },
                            icon = AppIcons.Table,
                            modifier = theModifier
                                .padding(top = AppTheme.spacing.l)
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}

private val ErrorColor = Color(0xFFfcd9de)