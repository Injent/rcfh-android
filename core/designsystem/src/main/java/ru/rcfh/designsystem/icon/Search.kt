package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Search: ImageVector
    get() {
        if (_Search != null) {
            return _Search!!
        }
        _Search = ImageVector.Builder(
            name = "Search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(22.705f, 21.253f)
                lineToRelative(-4.399f, -4.374f)
                curveToRelative(1.181f, -1.561f, 1.81f, -3.679f, 1.859f, -6.329f)
                curveToRelative(-0.105f, -6.095f, -3.507f, -9.473f, -9.588f, -9.513f)
                curveTo(4.423f, 1.076f, 1f, 4.649f, 1f, 10.549f)
                curveToRelative(0f, 6.195f, 3.426f, 9.512f, 9.589f, 9.548f)
                curveToRelative(2.629f, -0.016f, 4.739f, -0.626f, 6.303f, -1.805f)
                lineToRelative(4.403f, 4.379f)
                curveToRelative(0.518f, 0.492f, 1.131f, 0.291f, 1.414f, -0.004f)
                curveToRelative(0.383f, -0.398f, 0.388f, -1.025f, -0.004f, -1.414f)
                close()
                moveTo(3f, 10.567f)
                curveToRelative(0.097f, -5.035f, 2.579f, -7.499f, 7.576f, -7.53f)
                curveToRelative(4.949f, 0.032f, 7.503f, 2.571f, 7.589f, 7.512f)
                curveToRelative(-0.094f, 5.12f, -2.505f, 7.518f, -7.576f, 7.548f)
                curveToRelative(-5.077f, -0.03f, -7.489f, -2.422f, -7.589f, -7.53f)
                close()
            }
        }.build()

        return _Search!!
    }

@Suppress("ObjectPropertyName")
private var _Search: ImageVector? = null
