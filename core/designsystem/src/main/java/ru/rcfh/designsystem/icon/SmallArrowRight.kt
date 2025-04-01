package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val AppIcons.SmallArrowRight: ImageVector
    get() {
        if (_SmallArrowNext != null) {
            return _SmallArrowNext!!
        }
        _SmallArrowNext = ImageVector.Builder(
            name = "SmallArrowNext",
            defaultWidth = 6.dp,
            defaultHeight = 11.dp,
            viewportWidth = 6f,
            viewportHeight = 11f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(0.231f, 10.269f)
                curveTo(0.537f, 10.577f, 1.033f, 10.577f, 1.339f, 10.269f)
                lineTo(5.944f, 5.637f)
                curveTo(5.98f, 5.601f, 6f, 5.552f, 6f, 5.5f)
                curveTo(6f, 5.448f, 5.98f, 5.399f, 5.944f, 5.363f)
                lineTo(1.339f, 0.731f)
                curveTo(1.033f, 0.423f, 0.537f, 0.423f, 0.231f, 0.731f)
                curveTo(-0.077f, 1.041f, -0.077f, 1.545f, 0.231f, 1.856f)
                lineTo(3.855f, 5.5f)
                lineTo(0.231f, 9.144f)
                curveTo(-0.077f, 9.455f, -0.077f, 9.959f, 0.231f, 10.269f)
                close()
            }
        }.build()

        return _SmallArrowNext!!
    }

@Suppress("ObjectPropertyName")
private var _SmallArrowNext: ImageVector? = null
