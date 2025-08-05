package ru.rcfh.glpm.feature.form.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.rcfh.core.sdui.state.Table4State
import ru.rcfh.core.sdui.state.getFields
import ru.rcfh.core.sdui.state.getName
import ru.rcfh.core.sdui.state.getValue
import ru.rcfh.designsystem.component.ContentCell
import ru.rcfh.designsystem.component.HeaderCell
import ru.rcfh.designsystem.component.Table

@Composable
fun Table4View(
    state: Table4State,
    onEditRow: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastContentCellRowIdx = state.rows.size

    Table(
        rowCount = state.rows.size + 1 + state.totals.size,
        columnCount = state.colCount,
        modifier = modifier
    ) { rowIdx, colIdx ->

        // Headers
        when {
            rowIdx == 0 -> {
                val row = state.rows.getOrNull(0)?.getFields()

                HeaderCell(
                    text = row?.getOrNull(colIdx)?.getName() ?: "NULL"
                )
            }
            rowIdx in 1..lastContentCellRowIdx -> {
                // Content cells
                val row = state.rows.getOrNull(rowIdx - 1)?.getFields()

                ContentCell(text = row?.get(colIdx)?.getValue() ?: "NULL", onClick = { onEditRow(rowIdx - 1) })
            }
            rowIdx >= (state.rows.size + 1) - state.totals.size -> {
                // Total cells
                val row = state.rows.getOrNull(0)?.getFields()

                val totalIndex = rowIdx + 2 - state.rows.size - state.totals.size

                state.totals[totalIndex.coerceIn(0, state.totals.lastIndex)]
                    .values[row?.get(colIdx)?.id]
                    ?.let { value ->
                        ContentCell(text = value)
                    } ?: Box(Modifier)
            }
        }
    }
}