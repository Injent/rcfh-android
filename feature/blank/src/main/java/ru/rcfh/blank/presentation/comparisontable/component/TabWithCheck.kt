package ru.rcfh.blank.presentation.comparisontable.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.StatusSuccess
import ru.rcfh.designsystem.theme.AppTheme

@Composable
internal fun TabWithCheck(
    name: String,
    checked: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        if (selected) AppTheme.colorScheme.backgroundBrand else AppTheme.colorScheme.background1
    )
    val contentColor by animateColorAsState(
        if (selected) AppTheme.colorScheme.foregroundOnBrand else AppTheme.colorScheme.foreground1
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(48.dp)
            .clip(AppTheme.shapes.default)
            .clickable { onClick() }
            .background(containerColor)
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = expandHorizontally(spring()) + fadeIn(),
            exit = shrinkHorizontally(spring()) + fadeOut(),
            modifier = Modifier
        ) {
            Box(
                Modifier.padding(start = AppTheme.spacing.s)
            ) {
                Icon(
                    imageVector = AppIcons.StatusSuccess,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }

        BasicText(
            text = name,
            style = AppTheme.typography.callout,
            color = { contentColor },
            autoSize = TextAutoSize.StepBased(
                minFontSize = 10.sp,
                maxFontSize = AppTheme.typography.callout.fontSize,
                stepSize = 1.sp
            ),
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = AppTheme.spacing.s)
        )
    }
}