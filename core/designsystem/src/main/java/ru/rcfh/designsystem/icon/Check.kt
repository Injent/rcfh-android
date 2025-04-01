package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Check: ImageVector
    get() {
        if (_Check != null) {
            return _Check!!
        }
        _Check = ImageVector.Builder(
            name = "Check",
            defaultWidth = 16.dp,
            defaultHeight = 12.dp,
            viewportWidth = 16f,
            viewportHeight = 12f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(15.652f, 0.265f)
                curveTo(16.079f, 0.652f, 16.118f, 1.318f, 15.74f, 1.754f)
                lineTo(6.849f, 12f)
                lineTo(0.38f, 6.616f)
                curveTo(-0.062f, 6.249f, -0.128f, 5.584f, 0.232f, 5.133f)
                curveTo(0.592f, 4.682f, 1.242f, 4.614f, 1.684f, 4.982f)
                lineTo(6.618f, 9.087f)
                lineTo(14.195f, 0.355f)
                curveTo(14.573f, -0.081f, 15.226f, -0.121f, 15.652f, 0.265f)
                close()
            }
        }.build()

        return _Check!!
    }

@Suppress("ObjectPropertyName")
private var _Check: ImageVector? = null
