package ru.rcfh.glpm.feature.form.presentation.comparisontable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowSizeClass
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.component.AppSmallButton
import ru.rcfh.designsystem.component.AppSnackbarHost
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.component.SnackbarController
import ru.rcfh.designsystem.component.SnackbarMessage
import ru.rcfh.designsystem.icon.Add
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Check
import ru.rcfh.designsystem.icon.StatusSuccess
import ru.rcfh.designsystem.icon.Trash
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ScreenRotation
import ru.rcfh.designsystem.util.screenRotation
import ru.rcfh.glpm.feature.form.R
import ru.rcfh.glpm.feature.form.presentation.composables.TextFieldView
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.comparisonTableScreen() {
    composable<Screen.ComparisonTable> {
        ComparisonTableRoute(
            initialRowIndex = it.toRoute<Screen.ComparisonTable>().initialRowIdx
        )
    }
}

@Composable
private fun ComparisonTableRoute(
    initialRowIndex: Int?,
) {
    val viewModel = koinViewModel<ComparisonTableViewModel>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        ComparisonTableUiState.Loading -> {}
        is ComparisonTableUiState.Success -> {
            ComparisonTableScreen(
                onBack = viewModel::onBack,
                uiState = uiState as ComparisonTableUiState.Success,
                initialRowIndex = initialRowIndex
            )
        }
    }
}

@Composable
private fun ComparisonTableScreen(
    uiState: ComparisonTableUiState.Success,
    onBack: () -> Unit,
    initialRowIndex: Int?
) {
    val window = currentWindowAdaptiveInfo().windowSizeClass
    val screenRotation = screenRotation()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = initialRowIndex ?: 0
    ) { uiState.state.pages.size.coerceAtLeast(1) }

    Scaffold(
        topBar = {
            if (uiState.state.maxEntries > 1) {
                var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
                if (showDeleteDialog) {
                    DeleteDialog(
                        onConfirm = {
                            val pageToDelete = pagerState.currentPage

                            uiState.state.removePage(pagerState.currentPage)
                            showDeleteDialog = false

                            SnackbarController.show(
                                SnackbarMessage(
                                    text = context.getString(R.string.info_pageDeleted, pageToDelete + 1),
                                    icon = AppIcons.Check,
                                    tint = Color.White,
                                    displayTimeSeconds = 3
                                )
                            )
                        },
                        onDismiss = {
                            showDeleteDialog = false
                        },
                        page = pagerState.currentPage
                    )
                }

                TableTopBar(
                    currentPage = pagerState.currentPage,
                    pageCount = pagerState.pageCount,
                    maxEntries = uiState.state.maxEntries,
                    onChangePage = { page ->
                        scope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    },
                    onDeleteRequest = {
                        showDeleteDialog = true
                    },
                    onCreateRequest = {
                        uiState.state.addPage()
                        scope.launch {
                            delay(100)
                            pagerState.animateScrollToPage(
                                page = uiState.state.pages.lastIndex
                            )
                        }
                    },
                    onBack = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = AppTheme.spacing.l)
                        .padding(bottom = AppTheme.spacing.s)
                )
            }
        },
        snackbarHost = {
            AppSnackbarHost()
        },
        modifier = Modifier
            .then(
                if (screenRotation != ScreenRotation.NONE) {
                    Modifier.displayCutoutPadding().navigationBarsPadding()
                } else Modifier
            )
            .imePadding()
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) { pageIndex ->
            val page = uiState.state.pages.getOrElse(pageIndex) {
                uiState.state.addPage()
                uiState.state.pages[pageIndex]
            }
            var selectedTab by rememberSaveable {
                mutableIntStateOf(
                    if (page.useActual) 1 else 0
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = AppTheme.spacing.l)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TabWithCheck(
                        name = stringResource(R.string.origin),
                        checked = !page.useActual,
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabWithCheck(
                        name = stringResource(R.string.actual),
                        checked = page.useActual,
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
                AnimatedVisibility(
                    visible = selectedTab == 0 && page.useActual,
                ) {
                    SwitchSourceCard(
                        name = stringResource(
                            R.string.info_changeTo,
                            stringResource(R.string.origin)
                        ),
                        onClick = { page.useActual = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppTheme.spacing.s)
                    )
                }
                AnimatedVisibility(
                    visible = selectedTab == 1 && !page.useActual,
                ) {
                    SwitchSourceCard(
                        name = stringResource(
                            R.string.info_changeTo,
                            stringResource(R.string.actual)
                        ),
                        onClick = { page.useActual = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppTheme.spacing.s)
                    )
                }

                val listContentPadding = PaddingValues(
                    top = AppTheme.spacing.l,
                    bottom = innerPadding.calculateBottomPadding() + AppTheme.spacing.l
                )
                if (window.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                    Row {
                        FieldList(
                            items = page.origin,
                            contentPaddingValues = listContentPadding,
                            modifier = Modifier
                                .weight(1f)
                        )
                        VerticalDivider(
                            color = AppTheme.colorScheme.stroke2,
                            modifier = Modifier
                                .padding(horizontal = AppTheme.spacing.l)
                        )
                        FieldList(
                            items = page.actual,
                            contentPaddingValues = listContentPadding,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                } else {
                    FieldList(
                        items = if (selectedTab == 0) page.origin else page.actual,
                        contentPaddingValues = listContentPadding,
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldList(
    items: ImmutableList<TextState>,
    contentPaddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
        contentPadding = contentPaddingValues,
        modifier = modifier
            .fillMaxSize()
    ) {
        items(
            items = items
        ) { field ->
            TextFieldView(
                state = field,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 350.dp)
            )
        }
    }
}

@Composable
private fun TabWithCheck(
    name: String,
    checked: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        if (selected) AppTheme.colorScheme.backgroundBrand else AppTheme.colorScheme.background1
    )
    val contentColor by animateColorAsState(
        if (selected) AppTheme.colorScheme.foregroundOnBrand else AppTheme.colorScheme.foreground1
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(48.dp)
            .clip(AppTheme.shapes.default)
            .clickable { onClick() }
            .background(containerColor)
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = expandHorizontally(spring()) + fadeIn(),
            exit = shrinkHorizontally(spring()) + fadeOut(),
            modifier = Modifier
        ) {
            Box(
                Modifier.padding(start = AppTheme.spacing.s)
            ) {
                Icon(
                    imageVector = AppIcons.StatusSuccess,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }

        BasicText(
            text = name,
            style = AppTheme.typography.callout,
            color = { contentColor },
            autoSize = TextAutoSize.StepBased(
                minFontSize = 10.sp,
                maxFontSize = AppTheme.typography.callout.fontSize,
                stepSize = 1.sp
            ),
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = AppTheme.spacing.s)
        )
    }
}

@Composable
private fun SwitchSourceCard(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(48.dp)
            .background(AppTheme.colorScheme.background3, CircleShape)
            .padding(start = AppTheme.spacing.l)
    ) {
        Text(
            text = name,
            style = AppTheme.typography.callout,
            color = AppTheme.colorScheme.foreground1
        )

        AppTextButton(
            text = stringResource(R.string.button_yes),
            onClick = onClick,
            shape = CircleShape
        )
    }
}

@Composable
private fun DeleteDialog(
    page: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colorScheme.background1,
        title = {
            Text(
                text = stringResource(R.string.dialog_delete_title, page + 1),
                style = AppTheme.typography.headline1,
                color = AppTheme.colorScheme.foreground1
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dialog_delete_text),
                style = AppTheme.typography.callout,
                color = AppTheme.colorScheme.foreground1
            )
        },
        dismissButton = {
            AppSmallButton(
                text = stringResource(R.string.dialog_delete_dismiss),
                onClick = onDismiss,
            )
        },
        confirmButton = {
            AppTextButton(
                text = stringResource(R.string.dialog_delete_confirm),
                onClick = onConfirm,
                color = AppTheme.colorScheme.foregroundError
            )
        }
    )
}

@Composable
private fun TableTopBar(
    pageCount: Int,
    currentPage: Int,
    maxEntries: Int,
    onChangePage: (Int) -> Unit,
    onDeleteRequest: () -> Unit,
    onCreateRequest: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentPage) {
        listState.animateScrollToItem(
            index = (currentPage - 1).coerceAtLeast(0),
        )
    }

    Surface(
        color = AppTheme.colorScheme.background1,
        shape = CircleShape,
        modifier = modifier
            .height(54.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(AppTheme.spacing.xs)
        ) {
            AppBackButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(end = AppTheme.spacing.s)
            )
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.background2)
            ) {
                items(
                    count = pageCount
                ) { page ->
                    val selected = page == currentPage
                    val contentColor by animateColorAsState(
                        if (selected) AppTheme.colorScheme.foregroundOnBrand else AppTheme.colorScheme.foreground1
                    )
                    val containerColor by animateColorAsState(
                        if (selected) AppTheme.colorScheme.backgroundBrand else Color.Transparent
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(56.dp)
                            .height(48.dp)
                            .clip(CircleShape)
                            .clickable { onChangePage(page) }
                            .background(containerColor)
                    ) {
                        Text(
                            text = (page + 1).toString(),
                            style = AppTheme.typography.calloutButton,
                            color = contentColor,
                            maxLines = 1
                        )
                    }
                }
            }
            if (pageCount < maxEntries) {
                VerticalDivider(
                    color = AppTheme.colorScheme.stroke2,
                    modifier = Modifier
                        .padding(
                            horizontal = AppTheme.spacing.m,
                            vertical = AppTheme.spacing.s
                        )
                )
                AppIconButton(
                    containerColor = AppTheme.colorScheme.background2,
                    shape = CircleShape,
                    icon = AppIcon(
                        icon = AppIcons.Add,
                        onClick = onCreateRequest,
                        tint = Color.Black
                    ),
                    modifier = Modifier
                        .size(48.dp)
                )
            }

            if (pageCount > 1) {
                Spacer(Modifier.width(AppTheme.spacing.s))
                AppIconButton(
                    containerColor = AppTheme.colorScheme.paleRed,
                    shape = CircleShape,
                    icon = AppIcon(
                        icon = AppIcons.Trash,
                        onClick = onDeleteRequest,
                        tint = Color.Black
                    ),
                    modifier = Modifier
                        .size(48.dp)
                )
            }
        }
    }
}