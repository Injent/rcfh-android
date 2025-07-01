package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Minus: ImageVector
    get() {
        if (_Minus != null) {
            return _Minus!!
        }
        _Minus = ImageVector.Builder(
            name = "Minus",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(480f, 288f)
                horizontalLineTo(32f)
                curveToRelative(-17.67f, 0f, -32f, -14.33f, -32f, -32f)
                reflectiveCurveToRelative(14.33f, -32f, 32f, -32f)
                horizontalLineToRelative(448f)
                curveToRelative(17.67f, 0f, 32f, 14.33f, 32f, 32f)
                reflectiveCurveTo(497.67f, 288f, 480f, 288f)
                close()
            }
        }.build()

        return _Minus!!
    }

@Suppress("ObjectPropertyName")
private var _Minus: ImageVector? = null
