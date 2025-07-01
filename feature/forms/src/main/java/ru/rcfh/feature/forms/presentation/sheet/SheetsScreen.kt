package ru.rcfh.feature.forms.presentation.sheet

import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.designsystem.icon.Add
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ScreenRotation
import ru.rcfh.designsystem.util.rememberScreenRotation
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.feature.forms.presentation.composables.TableComposable
import ru.rcfh.feature.forms.state.LocalSavedStateHandle
import ru.rcfh.feature.forms.state.formState
import ru.rcfh.navigation.Screen
import ru.rcfh.navigation.toRoute

fun NavGraphBuilder.sheetScreen() {
    composable<Screen.Sheet>(
        enterTransition = {
            when (initialState.destination.toRoute()) {
                Screen.SheetRecord::class.qualifiedName -> {
                    slideInHorizontally { -it } + scaleIn(initialScale = 0.85f)
                }
                else -> slideInHorizontally { it }
            }
        },
        exitTransition = {
            when (targetState.destination.toRoute()) {
                Screen.SheetRecord::class.qualifiedName -> {
                    slideOutHorizontally { -it } + scaleOut(targetScale = 0.85f)
                }
                else -> slideOutHorizontally { it }
            }
        }
    ) {
        CompositionLocalProvider(
            LocalSavedStateHandle provides it.savedStateHandle
        ) {
            SheetsRoute()
        }
    }
}

@Composable
private fun SheetsRoute() {
    val viewModel = koinViewModel<SheetsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is SheetsUiState.Loading -> {

        }
        is SheetsUiState.Error -> {
            Text(text = "Error")
        }
        is SheetsUiState.Success -> {
            SheetsScreen(
                uiState = uiState as SheetsUiState.Success,
                onEditRequest = viewModel::navigateToSheetRecord
            )
        }
    }
}

@Composable
private fun SheetsScreen(
    uiState: SheetsUiState.Success,
    onEditRequest: (Int) -> Unit,
) {
    var tableState by formState<TableState>(
        documentId = uiState.documentId,
        formId = uiState.formId,
        template = uiState.tableTemplate
    )
    val screenRotation = rememberScreenRotation()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEditRequest(tableState.rows.size)
                },
                containerColor = AppTheme.colorScheme.backgroundBrand,
                contentColor = AppTheme.colorScheme.foregroundOnBrand,
                shape = AppTheme.shapes.large,
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colorScheme.tableHeader)
                .padding(innerPadding)
                .systemBarsPadding()
                .thenIf(screenRotation == ScreenRotation.LEFT) {
                    displayCutoutPadding()
                }
        ) {
            TableComposable(
                state = tableState,
                template = uiState.tableTemplate,
                onStateChange = { tableState = it },
                onEditRequest = onEditRequest,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}