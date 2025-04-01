package ru.rcfh.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object AppTheme {
    val colorScheme: ColorScheme
        @Composable get() = LocalAppColorScheme.current
    val shapes: Shapes
        @Composable get() = LocalShapes.current
    val spacing: Spacing
        @Composable get() = LocalSpacing.current
    val typography: Typography
        @Composable get() = LocalTypography.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
            window.decorView.setBackgroundColor(colorScheme.background2.toArgb())
        }
    }

    CompositionLocalProvider(
        LocalTypography provides AppTypography,
        LocalShapes provides Shapes(),
        LocalSpacing provides Spacing(),
        LocalAppColorScheme provides colorScheme
    ) {
        MaterialTheme(
            content = content,
            colorScheme = MaterialTheme.colorScheme.copy(
                background = colorScheme.background2,
                surface = colorScheme.background2,
            )
        )
    }
}