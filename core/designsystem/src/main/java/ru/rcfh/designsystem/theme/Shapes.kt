package ru.rcfh.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

class Shapes internal constructor(
    val default: RoundedCornerShape = RoundedCornerShape(12.dp),
    val large: RoundedCornerShape = RoundedCornerShape(16.dp),
    val small: RoundedCornerShape = RoundedCornerShape(8.dp),
    val extraSmall: RoundedCornerShape = RoundedCornerShape(4.dp),
    val defaultTopCarved: RoundedCornerShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    val defaultBottomCarved: RoundedCornerShape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
)

internal val LocalShapes = staticCompositionLocalOf<Shapes> {
    error("Shapes not provided")
}
