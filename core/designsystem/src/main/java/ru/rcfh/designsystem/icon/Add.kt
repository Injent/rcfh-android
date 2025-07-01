package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Add: ImageVector
    get() {
        if (_Plus != null) {
            return _Plus!!
        }
        _Plus = ImageVector.Builder(
            name = "Plus",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(480f, 224f)
                horizontalLineTo(288f)
                verticalLineTo(32f)
                curveToRelative(0f, -17.67f, -14.33f, -32f, -32f, -32f)
                reflectiveCurveToRelative(-32f, 14.33f, -32f, 32f)
                verticalLineToRelative(192f)
                horizontalLineTo(32f)
                curveToRelative(-17.67f, 0f, -32f, 14.33f, -32f, 32f)
                reflectiveCurveToRelative(14.33f, 32f, 32f, 32f)
                horizontalLineToRelative(192f)
                verticalLineToRelative(192f)
                curveToRelative(0f, 17.67f, 14.33f, 32f, 32f, 32f)
                reflectiveCurveToRelative(32f, -14.33f, 32f, -32f)
                verticalLineTo(288f)
                horizontalLineToRelative(192f)
                curveToRelative(17.67f, 0f, 32f, -14.33f, 32f, -32f)
                reflectiveCurveTo(497.67f, 224f, 480f, 224f)
                close()
            }
        }.build()

        return _Plus!!
    }

@Suppress("ObjectPropertyName")
private var _Plus: ImageVector? = null
