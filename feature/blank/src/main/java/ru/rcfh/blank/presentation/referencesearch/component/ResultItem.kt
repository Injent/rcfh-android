package ru.rcfh.blank.presentation.referencesearch.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Check
import ru.rcfh.designsystem.theme.AppTheme

@Composable
internal fun ResultItem(
    text: String,
    description: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(AppTheme.shapes.default)
            .clickable { onClick() }
            .padding(
                horizontal = AppTheme.spacing.m,
                vertical = AppTheme.spacing.l
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = AppTheme.spacing.l)
        ) {
            Text(
                text = text,
                style = AppTheme.typography.callout,
                color = AppTheme.colorScheme.foreground1,
                modifier = Modifier
                    .fillMaxWidth()
            )
            description?.let { desc ->
                Text(
                    text = desc,
                    style = AppTheme.typography.caption1,
                    color = AppTheme.colorScheme.foreground2,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        if (selected) {
            Icon(
                imageVector = AppIcons.Check,
                contentDescription = null,
                tint = AppTheme.colorScheme.link,
                modifier = Modifier
                    .size(18.dp)
            )
        }
    }
}

private val TopItemShape = RoundedCornerShape(
    topStart = 16.dp, topEnd = 16.dp,
    bottomStart = 8.dp, bottomEnd = 8.dp
)

private val BottomItemShape = RoundedCornerShape(
    topStart = 8.dp, topEnd = 8.dp,
    bottomStart = 16.dp, bottomEnd = 16.dp
)