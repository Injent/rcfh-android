package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val AppIcons.Calendar: ImageVector
    get() {
        if (_CalendarDay != null) {
            return _CalendarDay!!
        }
        _CalendarDay = ImageVector.Builder(
            name = "CalendarDay",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(8f, 12f)
                horizontalLineToRelative(-2f)
                curveToRelative(-1.103f, 0f, -2f, 0.897f, -2f, 2f)
                verticalLineToRelative(2f)
                curveToRelative(0f, 1.103f, 0.897f, 2f, 2f, 2f)
                horizontalLineToRelative(2f)
                curveToRelative(1.103f, 0f, 2f, -0.897f, 2f, -2f)
                verticalLineToRelative(-2f)
                curveToRelative(0f, -1.103f, -0.897f, -2f, -2f, -2f)
                close()
                moveTo(6f, 16f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                reflectiveCurveToRelative(-2f, 0f, -2f, 0f)
                close()
                moveTo(19f, 2f)
                horizontalLineToRelative(-1f)
                verticalLineToRelative(-1f)
                curveToRelative(0f, -0.552f, -0.447f, -1f, -1f, -1f)
                reflectiveCurveToRelative(-1f, 0.448f, -1f, 1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(-8f)
                verticalLineToRelative(-1f)
                curveToRelative(0f, -0.552f, -0.447f, -1f, -1f, -1f)
                reflectiveCurveToRelative(-1f, 0.448f, -1f, 1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(-1f)
                curveTo(2.243f, 2f, 0f, 4.243f, 0f, 7f)
                verticalLineToRelative(12f)
                curveToRelative(0f, 2.757f, 2.243f, 5f, 5f, 5f)
                horizontalLineToRelative(14f)
                curveToRelative(2.757f, 0f, 5f, -2.243f, 5f, -5f)
                lineTo(24f, 7f)
                curveToRelative(0f, -2.757f, -2.243f, -5f, -5f, -5f)
                close()
                moveTo(5f, 4f)
                horizontalLineToRelative(14f)
                curveToRelative(1.654f, 0f, 3f, 1.346f, 3f, 3f)
                verticalLineToRelative(1f)
                lineTo(2f, 8f)
                verticalLineToRelative(-1f)
                curveToRelative(0f, -1.654f, 1.346f, -3f, 3f, -3f)
                close()
                moveTo(19f, 22f)
                lineTo(5f, 22f)
                curveToRelative(-1.654f, 0f, -3f, -1.346f, -3f, -3f)
                verticalLineToRelative(-9f)
                horizontalLineToRelative(20f)
                verticalLineToRelative(9f)
                curveToRelative(0f, 1.654f, -1.346f, 3f, -3f, 3f)
                close()
            }
        }.build()

        return _CalendarDay!!
    }

@Suppress("ObjectPropertyName")
private var _CalendarDay: ImageVector? = null
