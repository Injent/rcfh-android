package ru.rcfh.glpm.feature.form.presentation.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.state.FieldState
import ru.rcfh.core.sdui.state.Table4State
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.glpm.feature.form.presentation.composables.Table4View
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
            tableState = uiState as FieldState,
            onBack = viewModel::onBack,
            onEditRow = viewModel::onEditRow
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TableScreen(
    tableState: FieldState,
    onBack: () -> Unit,
    onEditRow: (rowIdx: Int) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    AppBackButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(start = AppTheme.spacing.s)
                    )
                },
                title = {
                    Text(
                        text = "Таблица",
                        style = AppTheme.typography.title3,
                        color = AppTheme.colorScheme.foreground1,
                        modifier = Modifier
                            .padding(start = AppTheme.spacing.s)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colorScheme.tableHeader)
                .padding(innerPadding)
        ) {
            when (tableState.id) {
                "4table" -> {
                    Table4View(
                        state = tableState as Table4State,
                        onEditRow = onEditRow
                    )
                }
            }
        }
    }
}