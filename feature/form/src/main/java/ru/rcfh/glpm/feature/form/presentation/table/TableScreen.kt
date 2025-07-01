package ru.rcfh.glpm.feature.form.presentation.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ScreenRotation
import ru.rcfh.designsystem.util.screenRotation
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.glpm.feature.form.presentation.composables.TableView
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.tableScreen() {
    composable<Screen.Table> {
        TableRoute()
    }
}

@Composable
fun TableRoute() {
    val viewModel = koinViewModel<TableViewModel>()
    val uiState by viewModel.tableState.collectAsStateWithLifecycle()

    if (uiState != null) {
        TableScreen(
            tableState = uiState as TableState,
            onEditRow = viewModel::onEditRow
        )
    }
}

@Composable
private fun TableScreen(
    tableState: TableState,
    onEditRow: (rowIdx: Int) -> Unit,
) {
    val screenRotation = screenRotation()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
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
            TableView(
                state = tableState,
                onEditRequest = onEditRow,
                modifier = Modifier
            )
        }
    }
}