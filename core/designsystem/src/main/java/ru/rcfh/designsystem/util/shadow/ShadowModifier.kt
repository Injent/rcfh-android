package ru.rcfh.designsystem.util.shadow

import android.graphics.BlurMaskFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

private val SpotCard = Color(0x1F0C2975)

fun Modifier.basicShadow(shape: Shape = RectangleShape): Modifier {
    return if (SDK_INT < 28) {
        roundRectShadow(
            offset = DpOffset(0.dp, 2.dp),
            shape = shape,
            radius = 10.dp,
        )
    } else {
        boxShadow(
            color = SpotCard,
            blurRadius = 6.dp,
            spreadRadius = 0.dp,
            offset = DpOffset(0.dp, 2.dp),
            shape = shape,
            clip = false,
            inset = false,
            alpha = .5f
        )
    }
}

fun Modifier.roundRectShadow(
    color: Color = SpotCard,
    offset: DpOffset = DpOffset.Zero,
    shape: Shape = RectangleShape,
    radius: Dp,
    alpha: Float = 0.05f
) = this.then(
    ShadowDrawer(
        CustomShadowParams(
            name = "default",
            layers = listOf(
                Shadow(
                    dX = offset.x.value.toPx,
                    dY = offset.y.value.toPx,
                    radius = radius.value.toPx,
                    color = color.toArgb(),
                    colorAlpha = alpha,
                    linearGradientParams = GradientParams(
                        listOf(
                            GradientPointAndColorMultiplier(0f, 1f),
                            GradientPointAndColorMultiplier(0.85f, 0.1f),
                            GradientPointAndColorMultiplier(1f, 0f)
                        )
                    )
                )
            )
        ),
        shape,
    )
)

private class ShadowDrawer(
    private val customShadowParams: CustomShadowParams,
    private val shape: Shape,
) : DrawModifier {

    private val composeCompatShadowsRenderer = ComposeCompatShadowsRenderer()

    override fun ContentDrawScope.draw() {
        val density = Density(density, fontScale)

        val cornersParams = when (shape) {
            is RoundedCornerShape -> {
                CornersParams(
                    topLeft = shape.topStart.toPx(size, density),
                    topRight = shape.topEnd.toPx(size, density),
                    bottomLeft = shape.bottomStart.toPx(size, density),
                    bottomRight = shape.bottomEnd.toPx(size, density)
                )
            }
            else -> {
                drawContent()
                return
            }
        }

        customShadowParams.layers.forEach {
            composeCompatShadowsRenderer.paintCompatShadow(
                canvas = this,
                cornersParams = cornersParams,
                shadow = it
            )
        }
        drawContent()
    }
}

@Stable
fun Modifier.boxShadow(
    color: Color = SpotCard,
    blurRadius: Dp = 10.dp,
    spreadRadius: Dp = 0.dp,
    offset: DpOffset = DpOffset.Zero,
    shape: Shape = RectangleShape,
    clip: Boolean = true,
    inset: Boolean = false,
    alpha: Float = 1f
): Modifier {

    require(color.isSpecified) { "color must be specified." }
    require(blurRadius.isSpecified) { "blurRadius must be specified." }
    require(spreadRadius.isSpecified) { "spreadRadius must be specified." }
    require(blurRadius.value >= 0f) { "blurRadius can't be negative." }
    require(offset.isSpecified) { "offset must be specified." }

    return drawWithCache {
        onDrawWithContent {

            if (inset)
                drawContent()

            drawIntoCanvas { canvas ->

                val colorArgb = color.copy(color.alpha * alpha).toArgb()
                val hasBlurRadius = blurRadius.value.let { it.isFinite() && it != 0f }
                val paint = Paint()

                paint.asFrameworkPaint().let { frameworkPaint ->
                    if (hasBlurRadius) {
                        frameworkPaint.maskFilter = BlurMaskFilter(
                            blurRadius.toPx(),
                            BlurMaskFilter.Blur.NORMAL
                        )
                    }

                    frameworkPaint.color = colorArgb
                }

                val spreadRadiusPx = spreadRadius.toPx().let { spreadRadiusPx ->
                    when {
                        inset -> -spreadRadiusPx
                        else -> spreadRadiusPx
                    }
                }

                val hasSpreadRadius = spreadRadiusPx != 0f
                val size = size
                val layoutDirection = layoutDirection

                val density = Density(
                    density = density,
                    fontScale = fontScale
                )

                val shadowOutline = shape.createOutline(
                    size = when {
                        hasSpreadRadius -> size.let { (width, height) ->
                            (2 * spreadRadiusPx).let { outset ->
                                Size(
                                    width = width + outset,
                                    height = height + outset
                                )
                            }
                        }
                        else -> size
                    },
                    layoutDirection = layoutDirection,
                    density = density
                )

                val nativeCanvas = canvas.nativeCanvas
                val count = nativeCanvas.save()

                if (inset) {

                    val boxOutline = when {
                        hasSpreadRadius -> shape.createOutline(
                            size = size,
                            layoutDirection = layoutDirection,
                            density = density
                        )
                        else -> shadowOutline
                    }

                    canvas.clipToOutline(boxOutline)

                    val bounds = boxOutline.bounds

                    nativeCanvas.saveLayer(
                        bounds.left,
                        bounds.top,
                        bounds.right,
                        bounds.bottom,
                        NativePaint().apply {
                            colorFilter = ColorMatrixColorFilter(
                                ColorMatrix(
                                    floatArrayOf(
                                        1f, 0f, 0f, 0f, 0f,
                                        0f, 1f, 0f, 0f, 0f,
                                        0f, 0f, 1f, 0f, 0f,
                                        0f, 0f, 0f, -1f, 255f * color.alpha
                                    )
                                )
                            )
                        }
                    )
                }

                canvas.translate(
                    dx = offset.x.toPx() - spreadRadiusPx,
                    dy = offset.y.toPx() - spreadRadiusPx
                )

                canvas.drawOutline(
                    outline = shadowOutline,
                    paint = paint
                )

                nativeCanvas.restoreToCount(count)
            }

            if (!inset)
                drawContent()
        }
    }.run {
        when {
            clip -> clip(shape)
            else -> this
        }
    }
}

fun Canvas.clipToOutline(
    outline: Outline,
    clipOp: ClipOp = ClipOp.Intersect
) {
    when (outline) {
        is Outline.Generic ->
            clipPath(path = outline.path, clipOp = clipOp)
        is Outline.Rectangle ->
            clipRect(rect = outline.rect, clipOp = clipOp)
        is Outline.Rounded ->
            clipPath(
                path = Path()
                    .apply { addRoundRect(outline.roundRect) },
                clipOp = clipOp
            )
    }
}