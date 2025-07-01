package ru.rcfh.feature.forms.presentation.sheetrecord

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.model.Kind
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.CalculatedState
import ru.rcfh.core.sdui.state.RatioState
import ru.rcfh.core.sdui.state.RepeatableState
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ScreenRotation
import ru.rcfh.designsystem.util.rememberScreenRotation
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.feature.forms.presentation.composables.RepeatableComposable
import ru.rcfh.feature.forms.presentation.composables.TextFormComposable
import ru.rcfh.feature.forms.presentation.composables.UnaryFormComposable
import ru.rcfh.feature.forms.state.LocalSavedStateHandle
import ru.rcfh.feature.forms.state.formState
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.sheetRecordScreen() {
    composable<Screen.SheetRecord>(
        enterTransition = {
            slideInHorizontally { it }
        },
        exitTransition = {
            slideOutHorizontally { it }
        }
    ) {
        CompositionLocalProvider(
            LocalSavedStateHandle provides it.savedStateHandle
        ) {
            SheetRecordRoute()
        }
    }
}

@Composable
private fun SheetRecordRoute() {
    val viewModel = koinViewModel<SheetRecordViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        SheetRecordUiState.Loading -> {

        }
        is SheetRecordUiState.Error -> {

        }
        is SheetRecordUiState.Success -> {
            SheetRecordScreen(
                uiState = state
            )
        }
    }
}

@Composable
private fun SheetRecordScreen(
    uiState: SheetRecordUiState.Success
) {
    var tableState by formState<TableState>(
        documentId = uiState.documentId,
        formId = uiState.formId,
        template = uiState.tableTemplate
    )
    val screenRotation = rememberScreenRotation()

    Scaffold(
        modifier = Modifier
            .imePadding()
            .thenIf(screenRotation != ScreenRotation.NONE) {
                displayCutoutPadding()
            }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                vertical = AppTheme.spacing.l
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .focusGroup()
        ) {
            item {
                Text(
                    text = uiState.tableTemplate.name,
                    style = AppTheme.typography.title3,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier
                        .padding(
                            start = AppTheme.spacing.l,
                            bottom = AppTheme.spacing.xxxl
                        )
                )
            }
            itemsIndexed(
                items = uiState.tableTemplate.columns,
                key = { index, _ -> index }
            ) { colIdx, template ->
                val cell = tableState.rows
                    .getOrNull(uiState.rowIdx)
                    ?.getOrNull(colIdx)

                when (template) {
                    is Template.Repeatable -> {
                        val repeatableState = (cell as? RepeatableState) ?: template.createEmptyState()
                        RepeatableComposable(
                            state = repeatableState,
                            template = template,
                            onStateChange = { state ->
                                val newRows = tableState.rows.map { it.toMutableList() }.toMutableList()

                                val newRow = newRows.getOrElse(uiState.rowIdx) { mutableListOf() }.toMutableList()

                                while (newRow.size <= colIdx) {
                                    newRow.add(template.createEmptyState())
                                }
                                newRow[colIdx] = state

                                if (uiState.rowIdx in newRows.indices) {
                                    newRows[uiState.rowIdx] = newRow
                                } else {
                                    newRows.add(newRow)
                                }
                                tableState = tableState.copy(rows = newRows)
                            },
                            modifier = Modifier
                                .padding(vertical = AppTheme.spacing.l)
                        )
                    }
                    is Template.Import -> {

                    }
                    is Template.Calculated -> {
                        val calculatedState = (cell as? CalculatedState) ?: template.createEmptyState()
                        val lineColor = AppTheme.colorScheme.foreground2

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
                            modifier = Modifier
                                .drawWithCache {
                                    val pathEffect = PathEffect.dashPathEffect(
                                        intervals = floatArrayOf(10f, 5f),
                                        phase = 0f
                                    )

                                    onDrawWithContent {
                                        drawContent()
                                        if (uiState.tableTemplate.columns.getOrNull(colIdx + 1) is Template.Calculated) {
                                            drawLine(
                                                color = lineColor,
                                                start = Offset(x = 16.dp.toPx(), y = size.height),
                                                end = Offset(x = size.width - 16.dp.toPx(), y = size.height),
                                                strokeWidth = 1.2.dp.toPx(),
                                                pathEffect = pathEffect
                                            )
                                        }
                                    }
                                }
                                .background(AppTheme.colorScheme.background4)
                                .padding(AppTheme.spacing.l)
                        ) {
                            Text(
                                text = template.name.replace('\n', ' '),
                                style = AppTheme.typography.callout,
                                color = AppTheme.colorScheme.foreground1
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = remember(calculatedState) {
                                    (calculatedState.value.toFloatOrNull() ?: 0f)
                                        .roundAndTrim()
                                },
                                style = AppTheme.typography.callout,
                                color = AppTheme.colorScheme.foreground1,
                            )
                        }
                    }
                    is Template.Text -> {
                        if (template.kind !is Kind.Unary) {
                            val textState = (cell as? TextState) ?: template.createEmptyState()

                            TextFormComposable(
                                state = textState,
                                onStateChange = { state ->
                                    val newRows = tableState.rows.map { it.toMutableList() }.toMutableList()
                                    val newRow = newRows.getOrElse(uiState.rowIdx) { mutableListOf() }.toMutableList()

                                    while (newRow.size <= colIdx) {
                                        newRow.add(null)
                                    }
                                    newRow[colIdx] = state

                                    if (uiState.rowIdx in newRows.indices) {
                                        newRows[uiState.rowIdx] = newRow
                                    } else {
                                        newRows.add(newRow)
                                    }
                                    tableState = tableState.copy(rows = newRows)
                                },
                                template = template,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = AppTheme.spacing.l)
                                    .padding(bottom = AppTheme.spacing.l)
                            )
                        }
                    }
                    is Template.Ratio -> {
                        val ratioState = (cell as? RatioState) ?: template.createEmptyState()

                        Box(
                            modifier = Modifier
                                .padding(vertical = AppTheme.spacing.l)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .width(50.dp)
                                    .background(AppTheme.colorScheme.background2)
                                    .zIndex(1f)
                            ) {
                                Text(
                                    text = template.templates.first().name.substringBefore('\n'),
                                    style = AppTheme.typography.subheadline,
                                    color = AppTheme.colorScheme.foreground1,
                                    modifier = Modifier
                                        .padding(bottom = AppTheme.spacing.s)
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(50.dp)
                                        .background(AppTheme.colorScheme.background1, AppTheme.shapes.default)
                                ) {
                                    Spacer(Modifier.height(48.dp))
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .height(48.dp)
                                    ) {
                                        Text(
                                            text = ratioState.state.firstOrNull()?.value ?: "",
                                            style = AppTheme.typography.headline1,
                                            color = AppTheme.colorScheme.foreground1,
                                            modifier = Modifier
                                        )
                                    }
                                    Spacer(Modifier.height(48.dp))
                                }
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                                verticalAlignment = Alignment.CenterVertically,
                                contentPadding = PaddingValues(
                                    start = 50.dp + AppTheme.spacing.l,
                                    end = AppTheme.spacing.l,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                itemsIndexed(
                                    items = template.templates
                                ) { index, ratio ->
                                    if (index > 0) {
                                        UnaryFormComposable(
                                            state = ratioState.state.getOrNull(index) ?: ratio.createEmptyState(),
                                            onStateChange = { state ->
                                                val newRows = tableState.rows.map { it.toMutableList() }.toMutableList()
                                                val newRow = newRows.getOrElse(uiState.rowIdx) { mutableListOf() }.toMutableList()

                                                while (newRow.size <= colIdx) {
                                                    newRow.add(null)
                                                }

                                                val ratioRow = ratioState.state.toMutableList()
                                                while (ratioRow.size <= index) {
                                                    ratioRow.add(null)
                                                }
                                                ratioRow[index] = state

                                                val calc = (100 - (ratioRow
                                                    .mapIndexedNotNull { index, cell ->
                                                        if (index == 0) null
                                                        else cell?.value?.toFloatOrNull()
                                                    }
                                                    .sum()
                                                )).toInt()
                                                if (calc < 0) {
                                                    return@UnaryFormComposable
                                                }

                                                ratioRow[0] = TextState(id = template.templates.first().id, value = calc.toString())
                                                newRow[colIdx] = ratioState.copy(state = ratioRow)
                                                newRows[uiState.rowIdx] = newRow

                                                tableState = tableState.copy(rows = newRows)
                                            },
                                            minLimitReached = remember(ratioState) {
                                                ratioState.state.getOrNull(0)?.value?.toInt() == 100
                                            },
                                            maxLimitReached = remember(ratioState) {
                                                ratioState.state.getOrNull(0)?.value?.toInt() == 0
                                            },
                                            template = ratio
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

private fun Float.roundAndTrim(): String {
    return if (this == this.toInt().toFloat()) {
        this.toInt().toString()
    } else {
        "%.2f".format(this)
            .replace(",", ".")
            .replace(Regex("[.,]?0+$"), "")
    }
}