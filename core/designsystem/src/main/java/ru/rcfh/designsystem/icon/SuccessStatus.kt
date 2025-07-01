package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.StatusSuccess: ImageVector
    get() {
        if (_SuccessStatus != null) {
            return _SuccessStatus!!
        }
        _SuccessStatus = ImageVector.Builder(
            name = "Success",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(fill = SolidColor(Color(0xFF2DC052))) {
                moveTo(8f, 0f)
                lineTo(8f, 0f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16f, 8f)
                lineTo(16f, 8f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 16f)
                lineTo(8f, 16f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 8f)
                lineTo(0f, 8f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(4.312f, 6.955f)
                curveTo(4.712f, 6.574f, 5.345f, 6.591f, 5.725f, 6.991f)
                lineTo(7.203f, 8.548f)
                lineTo(10.275f, 5.311f)
                curveTo(10.655f, 4.911f, 11.288f, 4.895f, 11.689f, 5.275f)
                curveTo(12.089f, 5.655f, 12.106f, 6.288f, 11.725f, 6.689f)
                lineTo(7.928f, 10.689f)
                curveTo(7.739f, 10.887f, 7.477f, 11f, 7.203f, 11f)
                curveTo(6.928f, 11f, 6.666f, 10.887f, 6.477f, 10.689f)
                lineTo(4.275f, 8.368f)
                curveTo(3.895f, 7.968f, 3.911f, 7.335f, 4.312f, 6.955f)
                close()
            }
        }.build()

        return _SuccessStatus!!
    }

@Suppress("ObjectPropertyName")
private var _SuccessStatus: ImageVector? = null
