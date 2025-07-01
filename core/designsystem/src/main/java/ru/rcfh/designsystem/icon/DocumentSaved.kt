package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.DocumentSaved: ImageVector
    get() {
        if (_DocumentSaved != null) {
            return _DocumentSaved!!
        }
        _DocumentSaved = ImageVector.Builder(
            name = "DocumentSaved",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(19.95f, 5.536f)
                lineToRelative(-3.486f, -3.486f)
                arcToRelative(6.954f, 6.954f, 0f, isMoreThanHalf = false, isPositiveArc = false, -4.949f, -2.05f)
                horizontalLineToRelative(-4.515f)
                arcToRelative(5.006f, 5.006f, 0f, isMoreThanHalf = false, isPositiveArc = false, -5f, 5f)
                verticalLineToRelative(14f)
                arcToRelative(5.006f, 5.006f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5f, 5f)
                horizontalLineToRelative(10f)
                arcToRelative(5.006f, 5.006f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5f, -5f)
                verticalLineToRelative(-8.515f)
                arcToRelative(6.954f, 6.954f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.05f, -4.949f)
                close()
                moveTo(18.536f, 6.95f)
                arcToRelative(5.018f, 5.018f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.781f, 1.05f)
                horizontalLineToRelative(-4.317f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1f, -1f)
                verticalLineToRelative(-4.317f)
                arcToRelative(5.018f, 5.018f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.05f, 0.781f)
                close()
                moveTo(20f, 19f)
                arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3f, 3f)
                horizontalLineToRelative(-10f)
                arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3f, -3f)
                verticalLineToRelative(-14f)
                arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, -3f)
                horizontalLineToRelative(4.515f)
                curveToRelative(0.165f, 0f, 0.323f, 0.032f, 0.485f, 0.047f)
                verticalLineToRelative(4.953f)
                arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3f, 3f)
                horizontalLineToRelative(4.953f)
                curveToRelative(0.015f, 0.162f, 0.047f, 0.32f, 0.047f, 0.485f)
                close()
                moveTo(16.724f, 13.311f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.035f, 1.413f)
                lineToRelative(-3.589f, 3.414f)
                arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.226f, -0.017f)
                lineToRelative(-1.54f, -1.374f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.332f, -1.494f)
                lineToRelative(1.585f, 1.414f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.456f, 0.04f)
                lineToRelative(3.6f, -3.431f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.417f, 0.035f)
                close()
            }
        }.build()

        return _DocumentSaved!!
    }

@Suppress("ObjectPropertyName")
private var _DocumentSaved: ImageVector? = null
