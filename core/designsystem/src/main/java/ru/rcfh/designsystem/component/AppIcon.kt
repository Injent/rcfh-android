package ru.rcfh.designsystem.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class AppIcon(
    val icon: ImageVector,
    val onClick: (() -> Unit)? = null,
    val tint: Color? = null
)
