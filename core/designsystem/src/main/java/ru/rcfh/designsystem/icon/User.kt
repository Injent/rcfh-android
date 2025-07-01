package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.User: ImageVector
    get() {
        if (_User != null) {
            return _User!!
        }
        _User = ImageVector.Builder(
            name = "CircleUser 1",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(12f, 0f)
                curveTo(5.383f, 0f, 0f, 5.383f, 0f, 12f)
                reflectiveCurveToRelative(5.383f, 12f, 12f, 12f)
                reflectiveCurveToRelative(12f, -5.383f, 12f, -12f)
                reflectiveCurveTo(18.617f, 0f, 12f, 0f)
                close()
                moveTo(8f, 21.164f)
                verticalLineToRelative(-0.164f)
                curveToRelative(0f, -2.206f, 1.794f, -4f, 4f, -4f)
                reflectiveCurveToRelative(4f, 1.794f, 4f, 4f)
                verticalLineToRelative(0.164f)
                curveToRelative(-1.226f, 0.537f, -2.578f, 0.836f, -4f, 0.836f)
                reflectiveCurveToRelative(-2.774f, -0.299f, -4f, -0.836f)
                close()
                moveTo(17.925f, 20.051f)
                curveToRelative(-0.456f, -2.859f, -2.939f, -5.051f, -5.925f, -5.051f)
                reflectiveCurveToRelative(-5.468f, 2.192f, -5.925f, 5.051f)
                curveToRelative(-2.47f, -1.823f, -4.075f, -4.753f, -4.075f, -8.051f)
                curveTo(2f, 6.486f, 6.486f, 2f, 12f, 2f)
                reflectiveCurveToRelative(10f, 4.486f, 10f, 10f)
                curveToRelative(0f, 3.298f, -1.605f, 6.228f, -4.075f, 8.051f)
                close()
                moveTo(12f, 5f)
                curveToRelative(-2.206f, 0f, -4f, 1.794f, -4f, 4f)
                reflectiveCurveToRelative(1.794f, 4f, 4f, 4f)
                reflectiveCurveToRelative(4f, -1.794f, 4f, -4f)
                reflectiveCurveToRelative(-1.794f, -4f, -4f, -4f)
                close()
                moveTo(12f, 11f)
                curveToRelative(-1.103f, 0f, -2f, -0.897f, -2f, -2f)
                reflectiveCurveToRelative(0.897f, -2f, 2f, -2f)
                reflectiveCurveToRelative(2f, 0.897f, 2f, 2f)
                reflectiveCurveToRelative(-0.897f, 2f, -2f, 2f)
                close()
            }
        }.build()

        return _User!!
    }

@Suppress("ObjectPropertyName")
private var _User: ImageVector? = null
