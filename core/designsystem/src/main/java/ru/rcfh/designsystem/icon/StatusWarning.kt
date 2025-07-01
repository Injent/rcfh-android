package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.StatusWarning: ImageVector
    get() {
        if (_StatusWarning != null) {
            return _StatusWarning!!
        }
        _StatusWarning = ImageVector.Builder(
            name = "StatusWarning",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 20f,
            viewportHeight = 20f
        ).apply {
            path(fill = SolidColor(Color(0xFFFFB72A))) {
                moveTo(10f, 10f)
                moveToRelative(-10f, 0f)
                arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, 20f, 0f)
                arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, -20f, 0f)
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(10.12f, 5f)
                curveTo(10.729f, 5f, 11.241f, 5.333f, 11.241f, 6.125f)
                verticalLineTo(10.623f)
                curveTo(11.241f, 11.415f, 10.729f, 11.747f, 10.12f, 11.747f)
                curveTo(9.511f, 11.747f, 9f, 11.415f, 9f, 10.623f)
                verticalLineTo(6.125f)
                curveTo(9f, 5.333f, 9.511f, 5f, 10.12f, 5f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(10.12f, 12.751f)
                curveTo(10.729f, 12.751f, 11.241f, 13.259f, 11.241f, 13.863f)
                curveTo(11.241f, 14.492f, 10.729f, 15f, 10.12f, 15f)
                curveTo(9.511f, 15f, 9f, 14.492f, 9f, 13.887f)
                verticalLineTo(13.863f)
                curveTo(9f, 13.259f, 9.511f, 12.751f, 10.12f, 12.751f)
                close()
            }
        }.build()

        return _StatusWarning!!
    }

@Suppress("ObjectPropertyName")
private var _StatusWarning: ImageVector? = null
