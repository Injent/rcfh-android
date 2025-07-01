package ru.rcfh.feature.documents.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import ru.rcfh.designsystem.theme.AppTheme

@Composable
internal fun CardWithStatus(
    headline: String,
    text: String,
    onClick: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    trailingIcon: @Composable () -> Unit = {},
) {
    val containerColor = AppTheme.colorScheme.background1
    val selectedOverlayColor = AppTheme.colorScheme.link.copy(.03f)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape)
            .drawBehind {
                drawRect(
                    color = containerColor
                )
                if (selected) {
                    drawRect(
                        color = selectedOverlayColor
                    )
                }
            }
            .clickable { onClick() }
            .padding(
                horizontal = AppTheme.spacing.l,
                vertical = AppTheme.spacing.mNudge
            )
    ) {
        Text(
            text = buildAnnotatedString {
                append(headline)

                withStyle(
                    AppTheme.typography.caption1.toSpanStyle()
                        .copy(color = AppTheme.colorScheme.foreground2)
                ) {
                    appendLine()
                    append(text)
                }
            },
            style = AppTheme.typography.headline2,
            color = AppTheme.colorScheme.foreground1,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
        )
        trailingIcon()
    }
}