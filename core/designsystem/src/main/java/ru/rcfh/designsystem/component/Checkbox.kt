package ru.rcfh.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Check
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf

@Composable
fun AppCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    shape: Shape = RoundedCornerShape(6.dp),
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val (checkedColor, checkColor, borderColor) = when {
        checked && enabled -> arrayOf(AppTheme.colorScheme.foreground, AppTheme.colorScheme.foregroundOnBrand, null)
        !checked && enabled -> arrayOf(AppTheme.colorScheme.background1, null, AppTheme.colorScheme.foreground3)
        checked && !enabled -> arrayOf(AppTheme.colorScheme.foreground.copy(.5f), AppTheme.colorScheme.backgroundTouchable, null)
        else -> arrayOf(AppTheme.colorScheme.foreground3, null, AppTheme.colorScheme.foreground2)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .background(animateColorAsState(checkedColor!!).value, shape)
                .then(
                    borderColor?.let {
                        Modifier.border(1.dp, it, shape)
                    } ?: Modifier
                )
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onCheckedChange(!checked) }
                )
                .padding(1.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = checkColor ?: AppTheme.colorScheme.foreground
                )
            }
            checkColor?.let {
                Icon(
                    imageVector = AppIcons.Check,
                    contentDescription = null,
                    tint = it,
                    modifier = Modifier
                        .size(20.dp)
                        .thenIf(loading) { alpha(0f) }
                )
            }
        }
        text?.let {
            Text(
                text = it,
                style = AppTheme.typography.callout,
                color = AppTheme.colorScheme.foreground1,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}