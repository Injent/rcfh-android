package ru.rcfh.glpm.feature.form.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import ru.rcfh.core.sdui.state.CalculatedState
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf

@Composable
fun CalculatedView(
    state: CalculatedState,
    hasLine: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = AppTheme.colorScheme.foreground2

    Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
        modifier = modifier
            .thenIf(hasLine) {
                drawWithCache {
                    val pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(10f, 5f),
                        phase = 0f
                    )

                    onDrawWithContent {
                        drawContent()
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
            text = state.label.replace('\n', ' '),
            style = AppTheme.typography.callout,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )
        Text(
            text = state.value.ifEmpty { "—" },
            style = AppTheme.typography.callout,
            color = AppTheme.colorScheme.foreground1,
            maxLines = 1
        )
    }
}