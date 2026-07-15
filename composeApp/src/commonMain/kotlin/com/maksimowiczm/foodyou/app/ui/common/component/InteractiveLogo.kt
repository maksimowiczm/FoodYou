package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toPath
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.fastRoundToInt
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import foodyou.app.generated.resources.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun InteractiveLogo(
    modifier: Modifier = Modifier,
    iconFraction: Float = 0.33f,
    iconColor: Color = MaterialTheme.colorScheme.surface,
    rotationAnimationSpec: InfiniteRepeatableSpec<Float> =
        infiniteRepeatable(
            animation =
                tween(
                    easing = LinearEasing,
                    durationMillis = 1.minutes.inWholeMilliseconds.toInt(),
                ),
            repeatMode = RepeatMode.Restart,
        ),
    colorAnimationSpec: InfiniteRepeatableSpec<Float> =
        infiniteRepeatable(
            animation = tween(durationMillis = 20_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    clickable: Boolean = true,
    autoCycle: Boolean = false,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val coroutineScope = rememberCoroutineScope()
    val motionScheme = MaterialTheme.motionScheme
    val colorScheme = MaterialTheme.colorScheme

    val rotation =
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = rotationAnimationSpec,
        )

    val shapes = rememberShapes()
    val morphs =
        remember(shapes) {
            val pairs = mutableListOf<Pair<RoundedPolygon, RoundedPolygon>>()
            for (i in 1 until shapes.size) {
                pairs.add(Pair(shapes[i - 1], shapes[i]))
            }
            pairs.add(Pair(shapes.last(), shapes.first()))
            pairs.map { (start, end) -> Morph(start, end) }
        }
    val progress = rememberWrapAroundCounter(morphs.size.toFloat())
    val morph = remember { derivedStateOf { morphs[progress.value.toInt() % morphs.size] } }

    val meshProgress =
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = colorAnimationSpec,
        )

    if (autoCycle)
        LaunchedEffect(rotation) {
            snapshotFlow { rotation.value }
                .map { it.fastRoundToInt() }
                .distinctUntilChanged()
                .filter { it == 360 }
                .collectLatest { progress.increment(motionScheme.fastSpatialSpec()) }
        }

    val colors =
        remember(colorScheme) {
            listOf(
                colorScheme.primary,
                colorScheme.secondary,
                colorScheme.tertiary,
                colorScheme.tertiaryContainer,
                colorScheme.secondaryContainer,
                colorScheme.primaryContainer,
            )
        }

    val morphOutlineShape =
        remember(morph, progress, rotation) {
            object : Shape {
                override fun createOutline(
                    size: Size,
                    layoutDirection: LayoutDirection,
                    density: Density,
                ): Outline {
                    val matrix =
                        Matrix().apply {
                            reset()
                            val scaleFactor = size.minDimension * 0.75f
                            translate(size.width / 2f, size.height / 2f)
                            rotateZ(rotation.value)
                            scale(scaleFactor, scaleFactor)
                            translate(-0.5f, -0.5f)
                        }
                    val path = morph.value.toPath(progress.value % 1f)
                    path.transform(matrix)
                    return Outline.Generic(path)
                }
            }
        }

    val iconPainter = painterResource(Res.drawable.ic_sushi)
    Box(
        modifier =
            modifier.graphicsLayer {
                clip = true
                shape = morphOutlineShape
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            Modifier.fillMaxSize(.95f)
                .then(
                    if (clickable)
                        Modifier.clickable(interactionSource = null) {
                            coroutineScope.launch {
                                progress.increment(motionScheme.defaultSpatialSpec())
                            }
                        }
                    else Modifier
                )
        ) {
            val t = meshProgress.value * 2f * PI.toFloat()
            val colorCount = colors.size
            val colorProgress = (meshProgress.value * colorCount) % 1f
            val baseShift = (meshProgress.value * colorCount).toInt()

            fun getShiftedColor(index: Int): Color {
                val c1 = colors[(index + baseShift) % colorCount]
                val c2 = colors[(index + baseShift + 1) % colorCount]
                return lerp(c1, c2, colorProgress)
            }

            val gradientPainter =
                MeshGradientPainter(rows = 3, columns = 3) {
                    // Corners
                    setVertex(0, 0, Offset(0f, 0f), getShiftedColor(0))
                    setVertex(0, 3, Offset(1f, 0f), getShiftedColor(1))
                    setVertex(3, 0, Offset(0f, 1f), getShiftedColor(2))
                    setVertex(3, 3, Offset(1f, 1f), getShiftedColor(3))

                    // Top edge
                    setVertex(0, 1, Offset(0.33f + sin(t) * 0.03f, 0f), getShiftedColor(4))
                    setVertex(0, 2, Offset(0.66f + cos(t) * 0.03f, 0f), getShiftedColor(5))

                    // Bottom edge
                    setVertex(3, 1, Offset(0.33f + cos(t * 0.8f) * 0.03f, 1f), getShiftedColor(2))
                    setVertex(3, 2, Offset(0.66f + sin(t * 1.2f) * 0.03f, 1f), getShiftedColor(3))

                    // Left edge
                    setVertex(1, 0, Offset(0f, 0.33f + sin(t * 1.1f) * 0.03f), getShiftedColor(1))
                    setVertex(2, 0, Offset(0f, 0.66f + cos(t * 0.9f) * 0.03f), getShiftedColor(0))

                    // Right edge
                    setVertex(1, 3, Offset(1f, 0.33f + cos(t * 1.3f) * 0.03f), getShiftedColor(5))
                    setVertex(2, 3, Offset(1f, 0.66f + sin(t * 0.7f) * 0.03f), getShiftedColor(4))

                    // Inner vertices
                    setVertex(
                        1,
                        1,
                        Offset(0.33f + sin(t * 0.7f) * 0.12f, 0.33f + cos(t * 0.8f) * 0.12f),
                        getShiftedColor(0),
                    )
                    setVertex(
                        1,
                        2,
                        Offset(0.66f + cos(t * 0.9f) * 0.12f, 0.33f + sin(t * 0.7f) * 0.12f),
                        getShiftedColor(1),
                    )
                    setVertex(
                        2,
                        1,
                        Offset(0.33f + cos(t * 0.8f) * 0.12f, 0.66f + sin(t * 0.9f) * 0.12f),
                        getShiftedColor(2),
                    )
                    setVertex(
                        2,
                        2,
                        Offset(0.66f + sin(t * 0.7f) * 0.12f, 0.66f + cos(t * 0.8f) * 0.12f),
                        getShiftedColor(3),
                    )
                }

            with(gradientPainter) {
                draw(size)
            }

            val iconSize = Size(size.width * iconFraction, size.height * iconFraction)
            translate(
                left = (size.width - iconSize.width) / 2f,
                top = (size.height - iconSize.height) / 2f,
            ) {
                with(iconPainter) {
                    draw(
                        size = iconSize,
                        colorFilter = ColorFilter.tint(iconColor),
                    )
                }
            }
        }
    }
}

private val allowedShapes =
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

@Composable
private fun rememberShapes(): List<RoundedPolygon> {
    var shuffledIndices by rememberSaveable { mutableStateOf(allowedShapes.indices.shuffled()) }

    return remember(shuffledIndices) { shuffledIndices.map { allowedShapes[it] } }
}

@Stable
private class WrapAroundCounter(
    private val maxValue: Float,
    private val animatable: Animatable<Float, AnimationVector1D>,
) {
    val value: Float by derivedStateOf { animatable.value % maxValue }

    suspend fun increment(animationSpec: AnimationSpec<Float>) {
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
    var savedValue by rememberSaveable { mutableFloatStateOf(initialValue) }

    val animatable = remember { Animatable(savedValue) }

    LaunchedEffect(animatable.value) { savedValue = animatable.value }

    return remember(animatable, maxValue) {
        WrapAroundCounter(maxValue = maxValue, animatable = animatable)
    }
}
