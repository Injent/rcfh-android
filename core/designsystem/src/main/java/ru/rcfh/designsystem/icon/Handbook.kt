package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Handbook: ImageVector
    get() {
        if (_Handbook != null) {
            return _Handbook!!
        }
        _Handbook = ImageVector.Builder(
            name = "BookBookmark 1",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(17f, 0f)
                lineTo(7f, 0f)
                curveTo(4.243f, 0f, 2f, 2.243f, 2f, 5f)
                verticalLineToRelative(15f)
                curveToRelative(0f, 2.206f, 1.794f, 4f, 4f, 4f)
                horizontalLineToRelative(11f)
                curveToRelative(2.757f, 0f, 5f, -2.243f, 5f, -5f)
                lineTo(22f, 5f)
                curveToRelative(0f, -2.757f, -2.243f, -5f, -5f, -5f)
                close()
                moveTo(20f, 5f)
                verticalLineToRelative(11f)
                lineTo(8f, 16f)
                lineTo(8f, 2f)
                horizontalLineToRelative(4f)
                lineTo(12f, 10.347f)
                curveToRelative(0f, 0.623f, 0.791f, 0.89f, 1.169f, 0.395f)
                lineToRelative(1.331f, -1.743f)
                lineToRelative(1.331f, 1.743f)
                curveToRelative(0.378f, 0.495f, 1.169f, 0.228f, 1.169f, -0.395f)
                lineTo(17f, 2f)
                curveToRelative(1.654f, 0f, 3f, 1.346f, 3f, 3f)
                close()
                moveTo(6f, 2.184f)
                verticalLineToRelative(13.816f)
                curveToRelative(-0.732f, 0f, -1.409f, 0.212f, -2f, 0.556f)
                lineTo(4f, 5f)
                curveToRelative(0f, -1.302f, 0.839f, -2.402f, 2f, -2.816f)
                close()
                moveTo(17f, 22f)
                lineTo(6f, 22f)
                curveToRelative(-2.629f, -0.047f, -2.627f, -3.954f, 0f, -4f)
                horizontalLineToRelative(14f)
                verticalLineToRelative(1f)
                curveToRelative(0f, 1.654f, -1.346f, 3f, -3f, 3f)
                close()
            }
        }.build()

        return _Handbook!!
    }

@Suppress("ObjectPropertyName")
private var _Handbook: ImageVector? = null
