package ru.rcfh.designsystem.component

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ru.rcfh.designsystem.theme.AppTheme

@Composable
fun AppIconButton(
    icon: AppIcon,
    modifier: Modifier = Modifier,
    onCard: Boolean = false,
    containerColor: Color = if (onCard) {
        AppTheme.colorScheme.background2
    } else AppTheme.colorScheme.background4,
    shape: Shape = AppTheme.shapes.default,
    enabled: Boolean = true
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 40.dp) {
        FilledIconButton(
            onClick = icon.onClick ?: {},
            shape = shape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = containerColor,
                contentColor = icon.tint ?: AppTheme.colorScheme.foreground
            ),
            enabled = enabled,
            modifier = modifier
                .defaultMinSize(40.dp)
        ) {
            Icon(
                imageVector = icon.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}