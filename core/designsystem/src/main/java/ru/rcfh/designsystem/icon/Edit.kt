package ru.rcfh.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcons.Edit: ImageVector
    get() {
        if (_Edit != null) {
            return _Edit!!
        }
        _Edit = ImageVector.Builder(
            name = "Edit",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(22.987f, 5.451f)
                curveToRelative(-0.028f, -0.177f, -0.312f, -1.767f, -1.464f, -2.928f)
                curveToRelative(-1.157f, -1.132f, -2.753f, -1.412f, -2.931f, -1.44f)
                curveToRelative(-0.237f, -0.039f, -0.479f, 0.011f, -0.682f, 0.137f)
                curveToRelative(-0.118f, 0.073f, -2.954f, 1.849f, -8.712f, 7.566f)
                curveTo(3.135f, 14.807f, 1.545f, 17.213f, 1.48f, 17.312f)
                curveToRelative(-0.091f, 0.14f, -0.146f, 0.301f, -0.159f, 0.467f)
                lineToRelative(-0.319f, 4.071f)
                curveToRelative(-0.022f, 0.292f, 0.083f, 0.578f, 0.29f, 0.785f)
                curveToRelative(0.188f, 0.188f, 0.443f, 0.293f, 0.708f, 0.293f)
                curveToRelative(0.025f, 0f, 0.051f, 0f, 0.077f, -0.003f)
                lineToRelative(4.101f, -0.316f)
                curveToRelative(0.165f, -0.013f, 0.324f, -0.066f, 0.463f, -0.155f)
                curveToRelative(0.1f, -0.064f, 2.523f, -1.643f, 8.585f, -7.662f)
                curveToRelative(5.759f, -5.718f, 7.548f, -8.535f, 7.622f, -8.652f)
                curveToRelative(0.128f, -0.205f, 0.178f, -0.45f, 0.14f, -0.689f)
                close()
                moveTo(13.817f, 13.373f)
                curveToRelative(-4.93f, 4.895f, -7.394f, 6.78f, -8.064f, 7.263f)
                lineToRelative(-2.665f, 0.206f)
                lineToRelative(0.206f, -2.632f)
                curveToRelative(0.492f, -0.672f, 2.393f, -3.119f, 7.312f, -8.004f)
                curveToRelative(1.523f, -1.512f, 2.836f, -2.741f, 3.942f, -3.729f)
                curveToRelative(0.01f, 0.002f, 0.02f, 0.004f, 0.031f, 0.006f)
                curveToRelative(0.012f, 0.002f, 1.237f, 0.214f, 2.021f, 0.98f)
                curveToRelative(0.772f, 0.755f, 0.989f, 1.93f, 0.995f, 1.959f)
                curveToRelative(0f, 0.004f, 0.002f, 0.007f, 0.002f, 0.011f)
                curveToRelative(-0.999f, 1.103f, -2.245f, 2.416f, -3.78f, 3.94f)
                close()
                moveTo(19.126f, 7.689f)
                curveToRelative(-0.239f, -0.534f, -0.597f, -1.138f, -1.127f, -1.656f)
                curveToRelative(-0.524f, -0.513f, -1.134f, -0.861f, -1.674f, -1.093f)
                curveToRelative(1.139f, -0.95f, 1.908f, -1.516f, 2.309f, -1.797f)
                curveToRelative(0.419f, 0.124f, 1.049f, 0.377f, 1.481f, 0.8f)
                curveToRelative(0.453f, 0.456f, 0.695f, 1.081f, 0.81f, 1.469f)
                curveToRelative(-0.285f, 0.4f, -0.851f, 1.159f, -1.798f, 2.278f)
                close()
            }
        }.build()

        return _Edit!!
    }

@Suppress("ObjectPropertyName")
private var _Edit: ImageVector? = null
