package ru.rcfh.designsystem.theme

import android.app.Activity
import android.os.Build.VERSION.SDK_INT
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import ru.rcfh.core.model.DarkThemeConfig

object AppTheme {
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColorScheme.current
    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = LocalShapes.current
    val spacing: Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current
    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

@Composable
fun AppTheme(
    darkThemeConfig: DarkThemeConfig = DarkThemeConfig.LIGHT,
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    val colorScheme = lightColorScheme()

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            if (SDK_INT >= 29) {
                @Suppress("DEPRECATION")
                window.isStatusBarContrastEnforced = false
                window.isNavigationBarContrastEnforced = false
            }
            insetsController.isAppearanceLightStatusBars = !colorScheme.isDarkTheme
            insetsController.isAppearanceLightNavigationBars = !colorScheme.isDarkTheme
            window.decorView.setBackgroundColor(colorScheme.background2.toArgb())
        }
    }
    val shimmerTheme = defaultShimmerTheme.copy(
        shaderColors = listOf(
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 1.0f),
            Color.White.copy(alpha = 0.5f),
        )
    )

    CompositionLocalProvider(
        LocalTypography provides AppTypography,
        LocalShapes provides Shapes(),
        LocalSpacing provides Spacing(),
        LocalAppColorScheme provides colorScheme,
        LocalShimmerTheme provides shimmerTheme
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