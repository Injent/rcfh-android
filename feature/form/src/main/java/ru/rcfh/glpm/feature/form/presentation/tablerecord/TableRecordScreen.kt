package ru.rcfh.glpm.feature.form.presentation.tablerecord

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.state.CalculatedState
import ru.rcfh.core.sdui.state.ComparisonTableState
import ru.rcfh.core.sdui.state.LinkedState
import ru.rcfh.core.sdui.state.LocationState
import ru.rcfh.core.sdui.state.RatioState
import ru.rcfh.core.sdui.state.RepeatableState
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.glpm.feature.form.presentation.composables.CalculatedView
import ru.rcfh.glpm.feature.form.presentation.composables.LinkedView
import ru.rcfh.glpm.feature.form.presentation.composables.RatioView
import ru.rcfh.glpm.feature.form.presentation.composables.RepeatableView
import ru.rcfh.glpm.feature.form.presentation.composables.TextFieldView
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.tableRecordScreen() {
    composable<Screen.TableRecord> {
        TableRecordRoute()
    }
}

@Composable
fun TableRecordRoute() {
    val viewModel = koinViewModel<TableRecordViewModel>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TableRecordScreen(
        uiState = uiState
    )
}

@Composable
private fun TableRecordScreen(
    uiState: TableRecordUiState
) {
    Scaffold { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + AppTheme.spacing.l,
                bottom = innerPadding.calculateBottomPadding() + AppTheme.spacing.l
            ),
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .focusGroup()
        ) {
            item {
                Text(
                    text = uiState.tableName,
                    style = AppTheme.typography.title3,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier
                        .padding(
                            start = AppTheme.spacing.l,
                            bottom = AppTheme.spacing.l
                        )
                )
            }
            itemsIndexed(
                items = uiState.fields
            ) { index, field ->
                when (field) {
                    is CalculatedState -> {
                        CalculatedView(
                            state = field,
                            hasLine = uiState.fields.getOrNull(index + 1) is CalculatedState,
                            modifier = Modifier
                                .thenIf(uiState.fields.getOrNull(index - 1) !is CalculatedState) {
                                    padding(top = AppTheme.spacing.l)
                                }
                        )
                    }
                    is LinkedState -> {
                        LinkedView(
                            state = field,
                            hasLine = uiState.fields.getOrNull(index + 1) is LinkedState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppTheme.spacing.l)
                        )
                    }
                    is RatioState -> {
                        RatioView(
                            state = field,
                            modifier = Modifier
                                .padding(top = AppTheme.spacing.l)
                        )
                    }
                    is RepeatableState -> {
                        RepeatableView(
                            state = field,
                            modifier = Modifier
                                .padding(top = AppTheme.spacing.l)
                        )
                    }
                    is TextState -> {
                        TextFieldView(
                            state = field,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = AppTheme.spacing.l)
                                .padding(horizontal = AppTheme.spacing.l)
                        )
                    }
                    is ComparisonTableState,
                    is TableState,
                    is LocationState -> { /* No op */ }
                }
            }
        }
    }
}