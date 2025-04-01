package ru.rcfh.designsystem.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
inline fun Modifier.thenIf(condition: Boolean, block: @Composable Modifier.() -> Modifier): Modifier {
    return if (condition) then(block()) else this
}