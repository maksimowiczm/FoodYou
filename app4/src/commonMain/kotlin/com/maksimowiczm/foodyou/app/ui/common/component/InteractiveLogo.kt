package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.material3.toPath
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun InteractiveLogo(
    modifier: Modifier = Modifier,
    iconFraction: Float = 0.4f,
    iconColor: Color = MaterialTheme.colorScheme.surface,
    backgroundGradientColors: List<Color> =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
        ),
    animationSpec: InfiniteRepeatableSpec<Float> =
        InfiniteRepeatableSpec(
            animation =
                tween(
                    easing = LinearEasing,
                    durationMillis = 5.minutes.inWholeMilliseconds.toInt(),
                ),
            repeatMode = RepeatMode.Restart,
        ),
) {
    val infiniteTransition = rememberInfiniteTransition()
    val coroutineScope = rememberCoroutineScope()

    val rotation by
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = animationSpec,
        )

    val morphs = remember {
        val shapes =
            listOf(
                    MaterialShapes.Diamond,
                    MaterialShapes.Gem,
                    MaterialShapes.Oval,
                    MaterialShapes.Pill,
                    MaterialShapes.VerySunny,
                    MaterialShapes.Sunny,
                    MaterialShapes.Pentagon,
                    MaterialShapes.Burst,
                    MaterialShapes.Boom,
                    MaterialShapes.Flower,
                    MaterialShapes.PixelCircle,
                    MaterialShapes.Cookie4Sided,
                    MaterialShapes.Cookie6Sided,
                    MaterialShapes.Cookie7Sided,
                    MaterialShapes.Cookie9Sided,
                    MaterialShapes.Cookie12Sided,
                    MaterialShapes.Ghostish,
                    MaterialShapes.Clover4Leaf,
                    MaterialShapes.Clover8Leaf,
                )
                .shuffled()

        val pairs = mutableListOf<Pair<RoundedPolygon, RoundedPolygon>>()
        for (i in 1 until shapes.size) {
            pairs.add(Pair(shapes[i - 1], shapes[i]))
        }
        pairs.add(Pair(shapes.last(), shapes.first()))

        pairs.map { (start, end) -> Morph(start, end) }
    }
    val progress = rememberWrapAroundCounter(morphs.size.toFloat())
    val morph by remember {
        derivedStateOf {
            val index = (progress.value / 1f).toInt()

            if (index >= morphs.size) {
                morphs[0]
            } else {
                morphs[index]
            }
        }
    }

    val motionScheme = MaterialTheme.motionScheme

    val offset by
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                InfiniteRepeatableSpec(
                    animation = tween(durationMillis = 20_000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
        )

    val iconPainter = painterResource(Res.drawable.ic_sushi)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // This is hacky way to clip the canvas to a morphing shape because it can't be done
        // directly in draw scope because of ripple effect. Instead we use graphicsLayer to clip and
        // rotate the whole canvas. To keep the icon upright we rotate it back in the draw scope.
        Canvas(
            Modifier.fillMaxSize(.95f)
                .graphicsLayer {
                    clip = true
                    shape = GenericShape { size, _ ->
                        val path =
                            morph.toPath(progress.value % 1f).apply {
                                transform(Matrix().apply { scale(size.width, size.height) })
                            }

                        addPath(path)
                    }
                    rotationZ = rotation
                }
                .clickable(interactionSource = null, indication = ripple(bounded = true)) {
                    coroutineScope.launch { progress.increment(motionScheme.slowSpatialSpec()) }
                }
        ) {
            val brush =
                object : ShaderBrush() {
                    override fun createShader(size: Size): Shader {
                        val widthOffset = size.width * offset
                        val heightOffset = size.height * offset
                        return LinearGradientShader(
                            colors = backgroundGradientColors,
                            from = Offset(widthOffset, heightOffset),
                            to = Offset(widthOffset + size.width, heightOffset + size.height),
                            tileMode = TileMode.Mirror,
                        )
                    }
                }

            drawRect(brush)

            val iconSize = Size(size.width * iconFraction, size.height * iconFraction)
            rotate(degrees = -rotation, pivot = Offset(size.width / 2f, size.height / 2f)) {
                translate(
                    left = (size.width - iconSize.width) / 2f,
                    top = (size.height - iconSize.height) / 2f,
                ) {
                    with(iconPainter) {
                        draw(size = iconSize, colorFilter = ColorFilter.tint(iconColor))
                    }
                }
            }
        }
    }
}

@Stable
private class WrapAroundCounter(
    private val maxValue: Float,
    private val animatable: Animatable<Float, AnimationVector1D>,
) {
    val value: Float by derivedStateOf { animatable.value % maxValue }

    suspend fun increment(animationSpec: AnimationSpec<Float> = spring()) {
        animatable.animateTo(
            targetValue = (animatable.value + 1f).roundToInt().toFloat(),
            animationSpec = animationSpec,
        )
    }
}

@Composable
private fun rememberWrapAroundCounter(
    maxValue: Float,
    initialValue: Float = 0f,
): WrapAroundCounter {
    val animatable = remember(initialValue) { Animatable(initialValue) }

    val counter =
        remember(animatable, maxValue) {
            WrapAroundCounter(maxValue = maxValue, animatable = animatable)
        }

    return counter
}
