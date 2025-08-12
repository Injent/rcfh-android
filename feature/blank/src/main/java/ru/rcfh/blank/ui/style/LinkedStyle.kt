package ru.rcfh.blank.ui.style

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.ArrowTurnDownRight
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf

@Composable
fun LinkedStyle(
    label: String,
    value: String,
    hasLine: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        val lineColor = AppTheme.colorScheme.foreground3

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Max)
        ) {
            Icon(
                imageVector = AppIcons.ArrowTurnDownRight,
                contentDescription = null,
                tint = AppTheme.colorScheme.link,
                modifier = Modifier
                    .size(20.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
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
                                    start = Offset(x = 0f, y = size.height),
                                    end = Offset(x = size.width, y = size.height),
                                    strokeWidth = 1.2.dp.toPx(),
                                    pathEffect = pathEffect
                                )
                            }
                        }
                    }
                    .padding(vertical = AppTheme.spacing.s)
            ) {
                Text(
                    text = label,
                    style = AppTheme.typography.callout,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier
                        .padding(end = AppTheme.spacing.l)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = value.ifEmpty { "—" },
                    style = AppTheme.typography.callout,
                    color = AppTheme.colorScheme.foreground1
                )
            }
        }
    }
}