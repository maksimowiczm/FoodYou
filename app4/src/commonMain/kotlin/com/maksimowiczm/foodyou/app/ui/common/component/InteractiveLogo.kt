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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.material3.toPath
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun InteractiveLogo(
    modifier: Modifier = Modifier,
    iconFraction: Float = 0.5f,
    iconColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    animationSpec: InfiniteRepeatableSpec<Float> =
        InfiniteRepeatableSpec(
            animation = tween(easing = LinearEasing, durationMillis = 2 * 60 * 1000),
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
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .graphicsLayer {
                        rotationZ = rotation
                        clip = true
                        shape = MorphShape(morph = morph, percentage = progress.value % 1f)
                    }
                    .background(backgroundColor)
                    .clickable(interactionSource = interactionSource, indication = ripple()) {
                        coroutineScope.launch { progress.increment(motionScheme.slowSpatialSpec()) }
                    },
            content = {},
        )
        Icon(
            painter = painterResource(Res.drawable.ic_sushi),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(iconFraction),
            tint = iconColor,
        )
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

private class MorphShape(private val morph: Morph, private val percentage: Float) : Shape {

    private val matrix = Matrix()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        matrix.scale(size.width, size.height)

        val path = morph.toPath(progress = percentage)
        path.transform(matrix)

        return Outline.Generic(path)
    }
}
