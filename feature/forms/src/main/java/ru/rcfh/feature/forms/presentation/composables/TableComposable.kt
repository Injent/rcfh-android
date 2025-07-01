package ru.rcfh.feature.forms.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.CalculatedState
import ru.rcfh.core.sdui.state.RatioState
import ru.rcfh.core.sdui.state.RepeatableState
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.component.ContentCell
import ru.rcfh.designsystem.component.HeaderCell
import ru.rcfh.designsystem.component.Table
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Trash
import ru.rcfh.designsystem.theme.AppTheme

@Composable
fun TableComposable(
    state: TableState,
    template: Template.Table,
    onStateChange: (TableState) -> Unit,
    onEditRequest: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rowCount = state.rows.size

    Table(
        rowCount = rowCount + 2,
        columnCount = template.columns.size + 1,
        stickyRowCount = 1,
        modifier = modifier
    ) { rowIdx, columnIdx ->
        if (columnIdx == 0) {
            if (rowIdx == 0 || rowIdx == rowCount + 1) {
                Surface(
                    color = AppTheme.colorScheme.tableHeader
                ) { }
            } else {
                IconButton(
                    onClick = {
                        if (rowCount > 1) {
                            val newRows = state.rows.toMutableList()
                            newRows.removeAt(rowIdx - 1)
                            onStateChange(state.copy(rows = newRows))
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = AppTheme.colorScheme.foregroundError
                    )
                ) {
                    Icon(
                        imageVector = AppIcons.Trash,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        when {
            rowIdx == 0 && columnIdx > 0 -> {
                when (val column = template.columns[columnIdx - 1]) {
                    is Template.Repeatable -> {
                        HeaderCell(
                            text = column.name
                        )
                    }
                    is Template.Ratio -> {
                        Row {
                            column.templates.forEach { ratio ->
                                HeaderCell(
                                    text = ratio.name,
                                    modifier = Modifier
                                        .width(70.dp)
                                )
                            }
                        }
                    }
                    is Template.Text -> {
                        HeaderCell(
                            text = column.name,
                            modifier = Modifier
                                .widthIn(max = 320.dp)
                        )
                    }
                    is Template.Calculated -> {
                        HeaderCell(
                            text = column.name,
                            modifier = Modifier
                                .widthIn(max = 320.dp)
                        )
                    }
                    else -> Unit
                }
            }
            rowIdx == rowCount + 1 && columnIdx > 0 -> {
                Surface(
                    color = AppTheme.colorScheme.tableHeader
                ) {

                }
            }
            columnIdx > 0 -> {
                val cell = state.rows.getOrNull(rowIdx - 1)?.getOrNull(columnIdx - 1)

                when (val column = template.columns[columnIdx - 1]) {
                    is Template.Repeatable -> {
                        val repeatableState = (cell as? RepeatableState) ?: column.createEmptyState()

                        Column(
                            modifier = Modifier
                                .background(AppTheme.colorScheme.background1)
                                .widthIn(max = 320.dp)
                                .width(IntrinsicSize.Max)
                        ) {
                            repeatableState.groups.forEachIndexed { index, group ->
                                Column(
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = AppTheme.colorScheme.stroke1,
                                        )
                                        .clickable { onEditRequest(rowIdx - 1) }
                                        .padding(AppTheme.spacing.s)
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s)
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = AppTheme.typography.headline2,
                                            color = AppTheme.colorScheme.foreground1,
                                            modifier = Modifier
                                                .padding(horizontal = AppTheme.spacing.xs)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                column.templates.forEachIndexed { index, template ->
                                                    withStyle(
                                                        SpanStyle(fontWeight = FontWeight.SemiBold)
                                                    ) {
                                                        append("${template.name}: ")
                                                    }
                                                    append(group[index].ifEmpty { "-" })
                                                    if (index != column.templates.lastIndex) {
                                                        appendLine()
                                                    }
                                                }
                                            },
                                            style = AppTheme.typography.callout,
                                            color = AppTheme.colorScheme.foreground1
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is Template.Import -> {
                        val textState = (cell as? TextState) ?: column.createEmptyState()

                        ContentCell(
                            text = textState.value,
                            onClick = {
                                onEditRequest(rowIdx - 1)
                            }
                        )
                    }
                    is Template.Ratio -> {
                        val ratioState = (cell as? RatioState) ?: column.createEmptyState()
                        Box {
                            Row(modifier = Modifier.matchParentSize()) {
                                column.templates.forEachIndexed { index, _ ->
                                    ContentCell(
                                        text = ratioState.state.getOrNull(index)?.value ?: "",
                                        onClick = {
                                            onEditRequest(rowIdx - 1)
                                        },
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(70.dp)
                                    )
                                }
                            }
                        }
                    }
                    is Template.Text -> {
                        val textState = (cell as? TextState) ?: column.createEmptyState()

                        ContentCell(
                            text = textState.value,
                            onClick = {
                                onEditRequest(rowIdx - 1)
                            }
                        )
                    }
                    is Template.Calculated -> {
                        val calculatedState = (cell as? CalculatedState) ?: column.createEmptyState()

                        ContentCell(
                            text = calculatedState.value,
                            onClick = {
                                onEditRequest(rowIdx - 1)
                            }
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}