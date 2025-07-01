package ru.rcfh.designsystem.util

import android.os.Build.VERSION.SDK_INT
import android.view.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

enum class ScreenRotation {
    LEFT,
    NONE,
    RIGHT
}

@Suppress("DEPRECATION")
@ReadOnlyComposable
@Composable
fun screenRotation(): ScreenRotation {
    val activity = LocalContext.current.unwrap()

    val rotation = if (SDK_INT > 30) {
        activity.display?.rotation ?: 0
    } else {
        activity.windowManager.defaultDisplay.rotation
    }

    return when (rotation) {
        Surface.ROTATION_90 -> ScreenRotation.LEFT
        Surface.ROTATION_270 -> ScreenRotation.RIGHT
        else -> ScreenRotation.NONE
    }
}