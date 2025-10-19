package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    state: ColorPickerState = rememberColorPickerState(),
) {
    var isDragged by rememberSaveable { mutableStateOf(false) }

    val animateBorder by animateFloatAsState(if (isDragged) 5f else 3f)

    Canvas(
        modifier
            .defaultMinSize(minWidth = 250.dp, minHeight = 24.dp)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragged = true },
                    onDragEnd = { isDragged = false },
                    onDragCancel = { isDragged = false },
                ) { change, offset ->
                    state.position = (state.position + offset.x / size.width).coerceIn(0f, 1f)
                }
            }
    ) {
        val gradient =
            LinearGradientShader(
                from = Offset(0f, 0f),
                to = Offset(size.width, 0f),
                colors = ColorPickerDefaults.gradient,
            )

        drawRect(brush = ShaderBrush(gradient), size = size)

        val x =
            (size.width * state.position.coerceIn(0f, 1f)).coerceIn(
                minimumValue = size.minDimension / 2,
                maximumValue = size.width - size.minDimension / 2,
            )
        val center = Offset(x, size.height / 2)

        val selectedColor = interpolateGradient(ColorPickerDefaults.gradient, state.position)
        drawCircle(
            color = selectedColor,
            radius = (size.minDimension / 2) - animateBorder.dp.toPx() / 2,
            center = center,
        )

        drawCircle(
            color = Color.White,
            radius = (size.minDimension / 2) - animateBorder.dp.toPx() / 2,
            center = center,
            style = Stroke(animateBorder.dp.toPx()),
        )
    }
}

object ColorPickerDefaults {
    const val INITIAL_POSITION: Float = .5f

    val gradient =
        listOf(
            Color(0xFFFF0000),
            Color(0xFFFF7A00),
            Color(0xFFFFFF00),
            Color(0xFF00FF00),
            Color(0xFF00FFFF),
            Color(0xFF0000FF),
            Color(0xFF8B00FF),
            Color(0xFFFFFFFF),
            Color(0xFF000000),
        )
}

@Composable
fun rememberColorPickerState(
    initialPosition: Float = ColorPickerDefaults.INITIAL_POSITION
): ColorPickerState {
    val positionState = rememberSaveable { mutableStateOf(initialPosition) }
    return remember(positionState) { ColorPickerState(positionState) }
}

class ColorPickerState(positionState: MutableState<Float>) {
    var position: Float by positionState

    val selectedColor: Color by derivedStateOf {
        interpolateGradient(ColorPickerDefaults.gradient, position)
    }
}

private fun interpolateGradient(colors: List<Color>, position: Float): Color {
    val pos = position.coerceIn(0f, 1f)
    val scaledPos = pos * (colors.size - 1)
    val colorIndex = scaledPos.toInt()
    val nextIndex = (colorIndex + 1).coerceAtMost(colors.lastIndex)
    val fraction = scaledPos - colorIndex

    return lerp(colors[colorIndex], colors[nextIndex], fraction)
}

private fun lerp(start: Color, end: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * f,
        green = start.green + (end.green - start.green) * f,
        blue = start.blue + (end.blue - start.blue) * f,
        alpha = start.alpha + (end.alpha - start.alpha) * f,
    )
}

@Preview
@Composable
private fun ColorPickerPreview() {
    PreviewFoodYouTheme { ColorPicker() }
}
