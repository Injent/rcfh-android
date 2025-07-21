package ru.rcfh.designsystem.component

import android.view.SoundEffectConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Back
import ru.rcfh.designsystem.theme.AppTheme

@Composable
fun AppLargeButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    icon: AppIcon? = null,
    loading: Boolean = false,
    shape: Shape = AppTheme.shapes.default,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = {
            if (!loading) { onClick() }
        },
        shape = shape,
        enabled = enabled,
        color = if (enabled) {
            AppTheme.colorScheme.backgroundBrand
        } else AppTheme.colorScheme.backgroundDisabled,
        modifier = modifier
            .height(60.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
        ) {
            icon?.let {
                if (!loading) {
                    Icon(
                        imageVector = icon.icon,
                        contentDescription = null,
                        tint = icon.tint ?: AppTheme.colorScheme.foreground1
                    )
                    Spacer(Modifier.width(AppTheme.spacing.l))
                }
            }

            AppTextWithLoading(
                text = text,
                loadingSize = 26.dp,
                isLoading = loading,
                color = AppTheme.colorScheme.foregroundOnBrand
            )
        }
    }
}

@Composable
fun AppSecondaryLargeButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    icon: AppIcon? = null,
    loading: Boolean = false,
    shape: Shape = AppTheme.shapes.default,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = {
            if (!loading) { onClick() }
        },
        shape = shape,
        enabled = enabled,
        color = if (enabled) {
            AppTheme.colorScheme.background2
        } else AppTheme.colorScheme.backgroundDisabled,
        modifier = modifier
            .height(60.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
        ) {
            icon?.let {
                if (!loading) {
                    Icon(
                        imageVector = icon.icon,
                        contentDescription = null,
                        tint = icon.tint ?: AppTheme.colorScheme.foreground1
                    )
                    Spacer(Modifier.width(AppTheme.spacing.l))
                }
            }

            AppTextWithLoading(
                text = text,
                loadingSize = 26.dp,
                isLoading = loading,
                color = AppTheme.colorScheme.foreground1
            )
        }
    }
}

@Composable
fun AppSmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = AppTheme.colorScheme.backgroundBrand,
            contentColor = AppTheme.colorScheme.foregroundOnBrand,
            disabledContainerColor = AppTheme.colorScheme.backgroundDisabled,
            disabledContentColor = AppTheme.colorScheme.foreground2
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = AppTheme.typography.calloutButton,
        )
    }
}

@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape = AppTheme.shapes.default,
    color: Color = AppTheme.colorScheme.link,
) {
    Surface(
        onClick = {
            if (!loading) { onClick() }
        },
        shape = shape,
        enabled = enabled,
        color = Color.Transparent,
        modifier = modifier
            .height(48.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = AppTheme.spacing.l)
        ) {
            AppTextWithLoading(
                text = text,
                loadingSize = 26.dp,
                isLoading = loading,
                textStyle = AppTheme.typography.subheadlineButton,
                color = color
            )
        }
    }
}

@Composable
fun AppBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Surface(
        color = AppTheme.colorScheme.background1,
        shape = AppTheme.shapes.large,
        onClick = {
            onClick()
            view.playSoundEffect(SoundEffectConstants.CLICK)
        },
        modifier = modifier
            .size(40.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = AppIcons.Back,
                contentDescription = null,
                tint = AppTheme.colorScheme.foreground2,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun AppTextWithLoading(
    text: String,
    textStyle: TextStyle = AppTheme.typography.bodyButton,
    color: Color = LocalContentColor.current,
    loadingSize: Dp,
    isLoading: Boolean = false
) {
    CompositionLocalProvider(
        LocalContentColor provides color
    ) {
        Box {
            val alphaTransitionText by animateFloatAsState(
                targetValue = if (isLoading) 0f else 1f,
                animationSpec = tween(), label = ""
            )
            val scaleTransitionText by animateFloatAsState(
                targetValue = if (isLoading) 0.75f else 1f, label = ""
            )
            val alphaTransitionLoading by animateFloatAsState(
                targetValue = if (isLoading) 1f else 0f, label = ""
            )

            Text(
                text = text,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .alpha(alphaTransitionText)
                    .scale(scaleTransitionText)
                    .align(Alignment.Center),
                color = LocalContentColor.current
            )

            if (isLoading)
                CircularProgressIndicator(
                    color = color,
                    modifier = Modifier
                        .size(loadingSize)
                        .alpha(alphaTransitionLoading)
                        .align(Alignment.Center)
                )
        }
    }
}