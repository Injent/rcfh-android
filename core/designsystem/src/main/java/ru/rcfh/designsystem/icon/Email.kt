package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Email: ImageVector
    get() {
        if (_Email != null) {
            return _Email!!
        }
        _Email = ImageVector.Builder(
            name = "At",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(12f, 0f)
                arcTo(12.013f, 12.013f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 12f)
                curveToRelative(-0.126f, 9.573f, 11.159f, 15.429f, 18.9f, 9.817f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, -1.152f, -1.634f)
                curveTo(11.3f, 24.856f, 1.9f, 19.978f, 2f, 12f)
                curveTo(2.549f, -1.266f, 21.453f, -1.263f, 22f, 12f)
                verticalLineToRelative(2f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4f, 0f)
                lineTo(18f, 12f)
                curveTo(17.748f, 4.071f, 6.251f, 4.072f, 6f, 12f)
                arcToRelative(6.017f, 6.017f, 0f, isMoreThanHalf = false, isPositiveArc = false, 10.52f, 3.933f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = false, 24f, 14f)
                lineTo(24f, 12f)
                arcTo(12.013f, 12.013f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 0f)
                close()
                moveTo(12f, 16f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -8f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 16f)
                close()
            }
        }.build()

        return _Email!!
    }

@Suppress("ObjectPropertyName")
private var _Email: ImageVector? = null
