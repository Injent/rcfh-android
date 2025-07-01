package ru.rcfh.feature.documents.presentation.summarize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.StatusError
import ru.rcfh.designsystem.icon.StatusWarning
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ScreenRotation
import ru.rcfh.designsystem.util.screenRotation
import ru.rcfh.designsystem.util.shadow.basicShadow
import ru.rcfh.feature.documents.R
import ru.rcfh.feature.documents.presentation.composables.CardWithStatus
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.summarizeScreen() {
    composable<Screen.Summarize> {
        SummarizeRoute()
    }
}

@Composable
private fun SummarizeRoute() {
    val viewModel = koinViewModel<SummarizeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (!uiState.hasProblems) {
            viewModel.onBack()
            return@LifecycleEventEffect
        }
        viewModel.detectErrors()
    }

    SummarizeScreen(
        onBack = viewModel::onBack,
        onNavigateToProblem = viewModel::onNavigateToProblem,
        uiState = uiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SummarizeScreen(
    onBack: () -> Unit,
    onNavigateToProblem: (DetectedError) -> Unit,
    uiState: SummarizeUiState
) {
    val screenRotation = screenRotation()

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .basicShadow()
                    .background(AppTheme.colorScheme.background3)
                    .then(
                        if (screenRotation != ScreenRotation.NONE) {
                            Modifier.displayCutoutPadding().navigationBarsPadding()
                        } else Modifier
                    )
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .height(TopAppBarDefaults.MediumAppBarCollapsedHeight)
                    .padding(horizontal = AppTheme.spacing.l)
            ) {
                AppBackButton(
                    onClick = onBack
                )
                Text(
                    text = stringResource(R.string.title_summarize),
                    style = AppTheme.typography.title3,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier.padding(start = AppTheme.spacing.l)
                )
            }
        }
    ) { innerPadding ->
        val dividerColor = AppTheme.colorScheme.background2
        val dividerWidthPx = LocalDensity.current.run { 2.dp.toPx() }

        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + AppTheme.spacing.l
            ),
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (screenRotation != ScreenRotation.NONE) {
                        Modifier.displayCutoutPadding().navigationBarsPadding()
                    } else Modifier
                )
                .padding(horizontal = AppTheme.spacing.l)
        ) {
            if (uiState.report.severe.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.subheadline_severeProblem),
                        style = AppTheme.typography.subheadline,
                        color = AppTheme.colorScheme.foreground2,
                        modifier = Modifier.padding(vertical = AppTheme.spacing.l)
                    )
                }
            }

            itemsIndexed(
                items = uiState.report.severe
            ) { index, problem ->
                CardWithStatus(
                    headline = problem.error,
                    text = problem.name,
                    onClick = {
                        onNavigateToProblem(problem)
                    },
                    shape = when (index) {
                        0 -> if (uiState.report.severe.size == 1) {
                            AppTheme.shapes.default
                        } else {
                            AppTheme.shapes.defaultTopCarved
                        }
                        uiState.report.severe.lastIndex -> AppTheme.shapes.defaultBottomCarved
                        else -> RectangleShape
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = AppIcons.StatusError,
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            drawContent()
                            if (index != 0) {
                                drawLine(
                                    color = dividerColor,
                                    start = Offset.Zero,
                                    end = Offset(x = size.width, y = 0f),
                                    strokeWidth = dividerWidthPx
                                )
                            }
                        }
                )
            }

            if (uiState.report.warnings.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.subheadline_warningProblem),
                        style = AppTheme.typography.subheadline,
                        color = AppTheme.colorScheme.foreground2,
                        modifier = Modifier.padding(vertical = AppTheme.spacing.l)
                    )
                }
            }

            itemsIndexed(
                items = uiState.report.warnings
            ) { index, problem ->
                CardWithStatus(
                    headline = problem.error,
                    text = problem.name,
                    onClick = {
                        onNavigateToProblem(problem)
                    },
                    shape = when (index) {
                        0 -> if (uiState.report.warnings.size == 1) {
                            AppTheme.shapes.default
                        } else {
                            AppTheme.shapes.defaultTopCarved
                        }
                        uiState.report.warnings.lastIndex -> AppTheme.shapes.defaultBottomCarved
                        else -> RectangleShape
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = AppIcons.StatusWarning,
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            drawContent()
                            if (index != 0) {
                                drawLine(
                                    color = dividerColor,
                                    start = Offset.Zero,
                                    end = Offset(x = size.width, y = 0f),
                                    strokeWidth = dividerWidthPx
                                )
                            }
                        }
                )
            }
        }
    }
}