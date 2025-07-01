package ru.rcfh.glpm.feature.form.presentation.formnavigator

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.template.FormOptions
import ru.rcfh.core.sdui.template.FormTab
import ru.rcfh.designsystem.component.AppCard
import ru.rcfh.designsystem.component.AppSnackbarHost
import ru.rcfh.designsystem.component.SnackbarController
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ScreenRotation
import ru.rcfh.designsystem.util.screenRotation
import ru.rcfh.designsystem.util.shadow.basicShadow
import ru.rcfh.glpm.feature.form.presentation.form.FormRoute
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.formNavigatorScreen() {
    composable<Screen.FormNavigator> {
        FormNavigatorRoute()
    }
}

@Composable
private fun FormNavigatorRoute() {
    val viewModel = koinViewModel<FormNavigatorViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        SnackbarController.dismiss()
    }

    if (uiState.loading) {
        Box(Modifier.fillMaxSize()) {  }
    } else {
        FormNavigatorScreen(
            uiState = uiState,
            onSetOptionValue = viewModel::setOptionValue,
            onSelectTab = viewModel::selectTab
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun FormNavigatorScreen(
    uiState: FormNavigatorUiState,
    onSetOptionValue: (String, Int) -> Unit,
    onSelectTab: (Int) -> Unit,
) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    val screenRotation = screenRotation()
    val density = LocalDensity.current
    val destination by remember(uiState.selectedTabIndex) {
        val tab = uiState.formElements[uiState.selectedTabIndex]
        mutableStateOf(
            Screen.Form(
                documentId = uiState.documentId,
                formId = when (tab) {
                    is FormOptions -> uiState.formOptions[tab.optionId] ?: 0
                    is FormTab -> tab.formId
                },
                optionId = (tab as? FormOptions)?.optionId
            )
        )
    }

    Row {
        if (screenRotation != ScreenRotation.NONE) {
            FormNavigationRail(
                uiState = uiState,
                onSelect = onSelectTab,
                onSetOptionValue = onSetOptionValue,
                modifier = Modifier
                    .then(
                        when (screenRotation) {
                            ScreenRotation.LEFT -> Modifier.displayCutoutPadding()
                            ScreenRotation.RIGHT -> Modifier.navigationBarsPadding()
                            else -> Modifier
                        }
                    )
                    .statusBarsPadding()
                    .padding(vertical = AppTheme.spacing.l)
                    .padding(start = AppTheme.spacing.s)
            )
        }

        var topPadding by remember { mutableStateOf(0.dp) }
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            topBar = {
                if (screenRotation == ScreenRotation.NONE) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .displayCutoutPadding()
                            .navigationBarsPadding()
                    ) {
                        FormTopAppBar(
                            uiState = uiState,
                            onSelect = onSelectTab,
                            onSetOptionValue = onSetOptionValue,
                            modifier = Modifier
                                .padding(horizontal = AppTheme.spacing.l)
                                .padding(top = AppTheme.spacing.s)
                                .onSizeChanged { size ->
                                    topPadding = density.run { size.height.toDp() }
                                }
                        )
                    }
                }
            },
            snackbarHost = {
                AppSnackbarHost(
                    modifier = Modifier
                        .navigationBarsPadding()
                )
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner!!
            ) {
                AnimatedContent(
                    targetState = destination,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { route ->
                    FormRoute(
                        documentId = route.documentId,
                        formId = route.formId,
                        optionsId = route.optionId,
                        topPadding = topPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun FormNavigationRail(
    uiState: FormNavigatorUiState,
    onSelect: (Int) -> Unit,
    onSetOptionValue: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppTheme.colorScheme.background1,
        shape = AppTheme.shapes.large,
        modifier = modifier
            .fillMaxHeight()
            .basicShadow(AppTheme.shapes.large)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
            modifier = Modifier.padding(AppTheme.spacing.s)
        ) {
            FormItems(
                uiState = uiState,
                onSelect = onSelect,
                onSetOptionValue = onSetOptionValue,
                itemModifier = Modifier
                    .weight(1f)
                    .width(48.dp)
            )
        }
    }
}

@Composable
private fun FormTopAppBar(
    uiState: FormNavigatorUiState,
    onSelect: (Int) -> Unit,
    onSetOptionValue: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppTheme.colorScheme.background1,
        shape = AppTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .basicShadow(AppTheme.shapes.large)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(AppTheme.spacing.s)
        ) {
            FormItems(
                uiState = uiState,
                onSelect = onSelect,
                onSetOptionValue = onSetOptionValue,
                itemModifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
        }
    }
}

@Composable
private fun FormItems(
    uiState: FormNavigatorUiState,
    onSelect: (Int) -> Unit,
    onSetOptionValue: (String, Int) -> Unit,
    itemModifier: Modifier = Modifier
) {
    uiState.formElements.forEachIndexed { tabIndex, form ->
        val selected = tabIndex == uiState.selectedTabIndex
        val contentColor by animateColorAsState(
            if (selected) AppTheme.colorScheme.foregroundOnBrand
            else AppTheme.colorScheme.foreground1
        )
        val containerColor by animateColorAsState(
            if (selected) AppTheme.colorScheme.backgroundBrand
            else AppTheme.colorScheme.background2
        )
        val lineColor = AppTheme.colorScheme.foreground1
        val lineHorizontalPadding = LocalDensity.current.run { AppTheme.spacing.l.toPx() }
        val lineYOffset = LocalDensity.current.run { AppTheme.spacing.xs.toPx() }
        val strokeWidth = LocalDensity.current.run { 3.dp.toPx() }

        var showPopup by rememberSaveable { mutableStateOf(false) }

        Box(
            contentAlignment = Alignment.Center,
            modifier = itemModifier
                .drawWithContent {
                    drawContent()
                    if (form is FormOptions) {
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
                    if (form is FormOptions && uiState.formOptions[form.optionId] == null) {
                        showPopup = true
                    } else {
                        onSelect(tabIndex)
                    }
                }
                .background(containerColor)
        ) {
            Text(
                text = remember(uiState, form) {
                    when (form) {
                        is FormOptions -> {
                            val formId = uiState.formOptions[form.optionId]
                            form.tabs.find { it.formId == formId }?.let {
                                it.name
                            } ?: "?"
                        }
                        is FormTab -> form.name
                    }
                },
                style = AppTheme.typography.subheadlineButton,
                color = contentColor,
            )

            if (showPopup) {
                Popup(
                    onDismissRequest = { showPopup = false },
                    offset = IntOffset(
                        x = 0,
                        y = LocalDensity.current.run { -AppTheme.spacing.s.roundToPx() }
                    )
                ) {
                    AppCard(
                        contentPadding = PaddingValues(AppTheme.spacing.s),
                        shadowEnabled = true
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                        ) {
                            if (form is FormOptions) {
                                form.tabs.forEach { tab ->
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(AppTheme.shapes.default)
                                            .clickable {
                                                showPopup = false
                                                onSetOptionValue(form.optionId, tab.formId)
                                            }
                                            .background(AppTheme.colorScheme.background2)
                                    ) {
                                        Text(
                                            text = tab.name,
                                            style = AppTheme.typography.subheadlineButton,
                                            color = AppTheme.colorScheme.foreground1,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}