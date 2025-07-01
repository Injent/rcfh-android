package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Menu: ImageVector
    get() {
        if (_Menu != null) {
            return _Menu!!
        }
        _Menu = ImageVector.Builder(
            name = "MenuBurger 1",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 490.67f,
            viewportHeight = 490.67f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(469.33f, 224f)
                horizontalLineToRelative(-448f)
                curveTo(9.55f, 224f, 0f, 233.55f, 0f, 245.33f)
                curveToRelative(0f, 11.78f, 9.55f, 21.33f, 21.33f, 21.33f)
                horizontalLineToRelative(448f)
                curveToRelative(11.78f, 0f, 21.33f, -9.55f, 21.33f, -21.33f)
                curveTo(490.67f, 233.55f, 481.11f, 224f, 469.33f, 224f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(21.33f, 117.33f)
                horizontalLineToRelative(448f)
                curveToRelative(11.78f, 0f, 21.33f, -9.55f, 21.33f, -21.33f)
                reflectiveCurveToRelative(-9.55f, -21.33f, -21.33f, -21.33f)
                horizontalLineToRelative(-448f)
                curveTo(9.55f, 74.67f, 0f, 84.22f, 0f, 96f)
                reflectiveCurveTo(9.55f, 117.33f, 21.33f, 117.33f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(469.33f, 373.33f)
                horizontalLineToRelative(-448f)
                curveTo(9.55f, 373.33f, 0f, 382.89f, 0f, 394.67f)
                curveTo(0f, 406.45f, 9.55f, 416f, 21.33f, 416f)
                horizontalLineToRelative(448f)
                curveToRelative(11.78f, 0f, 21.33f, -9.55f, 21.33f, -21.33f)
                curveTo(490.67f, 382.89f, 481.11f, 373.33f, 469.33f, 373.33f)
                close()
            }
        }.build()

        return _Menu!!
    }

@Suppress("ObjectPropertyName")
private var _Menu: ImageVector? = null
