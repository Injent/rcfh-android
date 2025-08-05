//package ru.rcfh.glpm.feature.form.presentation.composables
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.IntrinsicSize
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.widthIn
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.unit.dp
//import ru.rcfh.core.sdui.state.CalculatedState
//import ru.rcfh.core.sdui.state.ComparisonTableState
//import ru.rcfh.core.sdui.state.LinkedState
//import ru.rcfh.core.sdui.state.LocationState
//import ru.rcfh.core.sdui.state.RatioState
//import ru.rcfh.core.sdui.state.RepeatableState
//import ru.rcfh.core.sdui.state.TableState
//import ru.rcfh.core.sdui.state.TextState
//import ru.rcfh.designsystem.component.ContentCell
//import ru.rcfh.designsystem.component.HeaderCell
//import ru.rcfh.designsystem.component.Table
//import ru.rcfh.designsystem.theme.AppTheme
//
//@Composable
//fun TableView(
//    state: TableState,
//    onEditRequest: (Int) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val rowCount = (state.rows.size) + state.totals.size
//
//    Box {
//        Table(
//            rowCount = rowCount,
//            columnCount = state.columnsCount,
//            stickyRowCount = 1,
//            modifier = modifier
//        ) { originRow, colIndex ->
//            // The first row is the headings so it should start counting from 1 index earlier.
//            val rowIndex = originRow - 1
//            println("$originRow $rowIndex $rowCount ${state.totals.size}")
//            //    6                   7           2
//            if (rowIndex >= (rowCount - (state.totals.size - 1))) {
//                val a = rowIndex - (rowCount - state.totals.size)
//                when (colIndex) {
//                    5 -> ContentCell(text = state.totals[a].name)
//                    6 -> {
//                        Row {
//                            state.totals[a].row.forEach { ratio ->
//                                ContentCell(
//                                    text = ratio.toString(),
//                                    modifier = Modifier
//                                        .width(70.dp)
//                                )
//                            }
//                        }
//                    }
//                    else -> ContentCell(text = "")
//                }
//            } else {
//                when (originRow) {
//                    0 -> {
//                        when (val column = state.rows[0][colIndex]) {
//                            is RepeatableState -> {
//                                HeaderCell(
//                                    text = column.name
//                                )
//                            }
//                            is RatioState -> {
//                                Row {
//                                    column.values.forEach { ratio ->
//                                        HeaderCell(
//                                            text = ratio.label,
//                                            modifier = Modifier
//                                                .width(70.dp)
//                                        )
//                                    }
//                                }
//                            }
//                            is TextState -> {
//                                HeaderCell(
//                                    text = column.label,
//                                    modifier = Modifier
//                                        .widthIn(max = 320.dp)
//                                )
//                            }
//                            is CalculatedState -> {
//                                HeaderCell(
//                                    text = column.label,
//                                    modifier = Modifier
//                                        .widthIn(max = 320.dp)
//                                )
//                            }
//                            is LinkedState -> {
//                                HeaderCell(
//                                    text = column.label,
//                                    modifier = Modifier
//                                        .widthIn(max = 320.dp)
//                                )
//                            }
//                            else -> Unit
//                        }
//                    }
//                    rowCount + 1 -> {
//                        when (val a = state.rows[0][colIndex]) {
//                            is RatioState -> {
//                                Box(modifier = Modifier) {
//                                    Row(modifier = Modifier.matchParentSize()) {
//                                        a.values.forEach { f ->
//                                            ContentCell(
//                                                onClick = {},
//                                                text = state.total[f.id]?.value ?: "",
//                                                modifier = Modifier
//                                                    .fillMaxHeight()
//                                                    .width(70.dp)
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                            else -> {
//                            }
//                        }
//                    }
//                    else -> {
//                        fun onEdit() = onEditRequest(rowIndex)
//
//                        when (val cellState = state.rows.getOrNull(rowIndex)?.getOrNull(colIndex)) {
//                            is CalculatedState -> {
//                                ContentCell(
//                                    text = cellState.value,
//                                    onClick = ::onEdit
//                                )
//                            }
//                            is LinkedState -> {
//                                ContentCell(
//                                    onClick = ::onEdit,
//                                    text = cellState.value
//                                )
//                            }
//                            is RatioState -> {
//                                RatioCell(
//                                    state = cellState,
//                                    onClick = ::onEdit
//                                )
//                            }
//                            is RepeatableState -> {
//                                RepeatableCell(
//                                    state = cellState,
//                                    onClick = ::onEdit
//                                )
//                            }
//                            is TextState -> {
//                                ContentCell(
//                                    text = cellState.value,
//                                    onClick = ::onEdit
//                                )
//                            }
//                            null,
//                            is LocationState,
//                            is TableState,
//                            is ComparisonTableState -> { /* No op */ }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun RatioCell(
//    state: RatioState,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(modifier = modifier) {
//        Row(modifier = Modifier.matchParentSize()) {
//            state.values.forEach { value ->
//                ContentCell(
//                    text = value.value,
//                    onClick = onClick,
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .width(70.dp)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun RepeatableCell(
//    state: RepeatableState,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier
//            .background(AppTheme.colorScheme.background1)
//            .widthIn(max = 320.dp)
//            .width(IntrinsicSize.Max)
//    ) {
//        state.groups.forEachIndexed { index, group ->
//            Column(
//                modifier = Modifier
//                    .border(
//                        width = 1.dp,
//                        color = AppTheme.colorScheme.stroke1,
//                    )
//                    .clickable { onClick() }
//                    .padding(AppTheme.spacing.s)
//                    .fillMaxWidth()
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s)
//                ) {
//                    Text(
//                        text = "${index + 1}",
//                        style = AppTheme.typography.headline2,
//                        color = AppTheme.colorScheme.foreground1,
//                        modifier = Modifier
//                            .padding(horizontal = AppTheme.spacing.xs)
//                    )
//                    Text(
//                        text = buildAnnotatedString {
//                            group.forEachIndexed { fieldIndex, state ->
//                                if (state is TextState) {
//                                    withStyle(
//                                        SpanStyle(fontWeight = FontWeight.SemiBold)
//                                    ) {
//                                        append("${state.label}: ")
//                                    }
//                                    append(state.value.ifEmpty { "-" })
//                                    if (fieldIndex != group.lastIndex) {
//                                        appendLine()
//                                    }
//                                }
//                            }
//                        },
//                        style = AppTheme.typography.callout,
//                        color = AppTheme.colorScheme.foreground1
//                    )
//                }
//            }
//        }
//    }
//}