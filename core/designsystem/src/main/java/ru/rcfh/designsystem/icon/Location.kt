package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Location: ImageVector
    get() {
        if (_Location != null) {
            return _Location!!
        }
        _Location = ImageVector.Builder(
            name = "Location",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(12f, 6f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = true, isPositiveArc = false, 4f, 4f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 6f)
                close()
                moveTo(12f, 12f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 2f, -2f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 12f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(12f, 24f)
                arcToRelative(5.271f, 5.271f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.311f, -2.2f)
                curveToRelative(-3.811f, -5.257f, -5.744f, -9.209f, -5.744f, -11.747f)
                arcToRelative(10.055f, 10.055f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20.11f, 0f)
                curveToRelative(0f, 2.538f, -1.933f, 6.49f, -5.744f, 11.747f)
                arcTo(5.271f, 5.271f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 24f)
                close()
                moveTo(12f, 2.181f)
                arcToRelative(7.883f, 7.883f, 0f, isMoreThanHalf = false, isPositiveArc = false, -7.874f, 7.874f)
                curveToRelative(0f, 2.01f, 1.893f, 5.727f, 5.329f, 10.466f)
                arcToRelative(3.145f, 3.145f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5.09f, 0f)
                curveToRelative(3.436f, -4.739f, 5.329f, -8.456f, 5.329f, -10.466f)
                arcTo(7.883f, 7.883f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 2.181f)
                close()
            }
        }.build()

        return _Location!!
    }

@Suppress("ObjectPropertyName")
private var _Location: ImageVector? = null
