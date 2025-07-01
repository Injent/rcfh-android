package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Guide: ImageVector
    get() {
        if (_Guide != null) {
            return _Guide!!
        }
        _Guide = ImageVector.Builder(
            name = "Guide",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(17f, 10f)
                curveToRelative(-3.86f, 0f, -7f, 3.14f, -7f, 7f)
                reflectiveCurveToRelative(3.14f, 7f, 7f, 7f)
                reflectiveCurveToRelative(7f, -3.14f, 7f, -7f)
                reflectiveCurveToRelative(-3.14f, -7f, -7f, -7f)
                close()
                moveTo(17f, 22f)
                curveToRelative(-2.757f, 0f, -5f, -2.243f, -5f, -5f)
                reflectiveCurveToRelative(2.243f, -5f, 5f, -5f)
                reflectiveCurveToRelative(5f, 2.243f, 5f, 5f)
                reflectiveCurveToRelative(-2.243f, 5f, -5f, 5f)
                close()
                moveTo(16f, 17f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-3f)
                close()
                moveTo(16f, 14f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-2f)
                close()
                moveTo(9.518f, 22f)
                lineTo(3f, 22f)
                curveToRelative(-0.551f, 0f, -1f, -0.448f, -1f, -1f)
                reflectiveCurveToRelative(0.449f, -1f, 1f, -1f)
                horizontalLineToRelative(5.523f)
                curveToRelative(-0.226f, -0.638f, -0.388f, -1.306f, -0.464f, -2f)
                horizontalLineToRelative(-2.059f)
                lineTo(6f, 2f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(6.058f)
                curveToRelative(0.695f, 0.077f, 1.362f, 0.239f, 2f, 0.464f)
                lineTo(20f, 2f)
                curveToRelative(0f, -1.103f, -0.897f, -2f, -2f, -2f)
                lineTo(3f, 0f)
                curveTo(1.346f, 0f, 0f, 1.346f, 0f, 3f)
                verticalLineToRelative(18f)
                curveToRelative(0f, 1.654f, 1.346f, 3f, 3f, 3f)
                horizontalLineToRelative(8.349f)
                curveToRelative(-0.706f, -0.571f, -1.325f, -1.244f, -1.831f, -2f)
                close()
                moveTo(2f, 3f)
                curveToRelative(0f, -0.551f, 0.449f, -1f, 1f, -1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(16f)
                horizontalLineToRelative(-1f)
                curveToRelative(-0.352f, 0f, -0.686f, 0.072f, -1f, 0.184f)
                lineTo(2f, 3f)
                close()
            }
        }.build()

        return _Guide!!
    }

@Suppress("ObjectPropertyName")
private var _Guide: ImageVector? = null
