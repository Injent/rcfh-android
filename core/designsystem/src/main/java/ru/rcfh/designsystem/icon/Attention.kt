package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Attention: ImageVector
    get() {
        if (_Attention != null) {
            return _Attention!!
        }
        _Attention = ImageVector.Builder(
            name = "Attention",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 20f,
            viewportHeight = 20f
        ).apply {
            path(fill = SolidColor(Color(0xFF185AC5))) {
                moveTo(10.75f, 4.469f)
                curveTo(10.75f, 4.041f, 10.414f, 3.694f, 10f, 3.694f)
                curveTo(9.586f, 3.694f, 9.25f, 4.041f, 9.25f, 4.469f)
                verticalLineTo(11.359f)
                curveTo(9.25f, 11.788f, 9.586f, 12.134f, 10f, 12.134f)
                curveTo(10.414f, 12.134f, 10.75f, 11.788f, 10.75f, 11.359f)
                verticalLineTo(4.469f)
                close()
            }
            path(fill = SolidColor(Color(0xFF185AC5))) {
                moveTo(10f, 14.029f)
                curveTo(10.414f, 14.029f, 10.8f, 14.376f, 10.8f, 14.804f)
                verticalLineTo(14.919f)
                curveTo(10.8f, 15.347f, 10.414f, 15.694f, 10f, 15.694f)
                curveTo(9.586f, 15.694f, 9.2f, 15.347f, 9.2f, 14.919f)
                verticalLineTo(14.804f)
                curveTo(9.2f, 14.376f, 9.586f, 14.029f, 10f, 14.029f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF185AC5)),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(20f, 10f)
                curveTo(20f, 15.523f, 15.523f, 20f, 10f, 20f)
                curveTo(4.477f, 20f, 0f, 15.523f, 0f, 10f)
                curveTo(0f, 4.477f, 4.477f, 0f, 10f, 0f)
                curveTo(15.523f, 0f, 20f, 4.477f, 20f, 10f)
                close()
                moveTo(18.5f, 10f)
                curveTo(18.5f, 14.694f, 14.694f, 18.5f, 10f, 18.5f)
                curveTo(5.306f, 18.5f, 1.5f, 14.694f, 1.5f, 10f)
                curveTo(1.5f, 5.306f, 5.306f, 1.5f, 10f, 1.5f)
                curveTo(14.694f, 1.5f, 18.5f, 5.306f, 18.5f, 10f)
                close()
            }
        }.build()

        return _Attention!!
    }

@Suppress("ObjectPropertyName")
private var _Attention: ImageVector? = null
