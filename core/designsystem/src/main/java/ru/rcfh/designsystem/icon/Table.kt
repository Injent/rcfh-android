package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Table: ImageVector
    get() {
        if (_TableList != null) {
            return _TableList!!
        }
        _TableList = ImageVector.Builder(
            name = "TableList",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(21f, 2f)
                lineTo(3f, 2f)
                curveToRelative(-1.654f, 0f, -3f, 1.346f, -3f, 3f)
                verticalLineToRelative(17f)
                horizontalLineToRelative(24f)
                lineTo(24f, 5f)
                curveToRelative(0f, -1.654f, -1.346f, -3f, -3f, -3f)
                close()
                moveTo(22f, 5f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(-14f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(13f)
                curveToRelative(0.552f, 0f, 1f, 0.449f, 1f, 1f)
                close()
                moveTo(8f, 10f)
                horizontalLineToRelative(14f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(-14f)
                verticalLineToRelative(-4f)
                close()
                moveTo(6f, 14f)
                lineTo(2f, 14f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                close()
                moveTo(3f, 4f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(4f)
                lineTo(2f, 8f)
                verticalLineToRelative(-3f)
                curveToRelative(0f, -0.551f, 0.448f, -1f, 1f, -1f)
                close()
                moveTo(2f, 16f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(4f)
                lineTo(2f, 20f)
                verticalLineToRelative(-4f)
                close()
                moveTo(8f, 20f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(14f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(-14f)
                close()
            }
        }.build()

        return _TableList!!
    }

@Suppress("ObjectPropertyName")
private var _TableList: ImageVector? = null
