package com.maksimowiczm.foodyou.app.ui.common

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Shape

/**
 * A class that holds the shapes for different interaction states.
 *
 * @param shape the default shape to use
 * @param pressedShape the shape to use when the component is pressed
 * @param focusedShape the shape to use when the component is focused
 * @param hoveredShape the shape to use when the component is hovered
 * @param draggedShape the shape to use when the component is being dragged
 */
@Immutable
class InteractionShapes(
    val shape: CornerBasedShape,
    val pressedShape: CornerBasedShape = shape,
    val focusedShape: CornerBasedShape = shape,
    val hoveredShape: CornerBasedShape = shape,
    val draggedShape: CornerBasedShape = shape,
) {
    fun shapeForInteraction(
        pressed: Boolean,
        focused: Boolean,
        hovered: Boolean,
        dragged: Boolean,
    ): CornerBasedShape =
        when {
            pressed -> pressedShape
            dragged -> draggedShape
            focused -> focusedShape
            hovered -> hoveredShape
            else -> shape
        }
}

/**
 * Resolves and remembers a [Shape] that smoothly morphs between different [CornerBasedShape]s based
 * on the current interaction state.
 *
 * @param shapes the [InteractionShapes] to use
 * @param interactionSource the [MutableInteractionSource] to collect interaction states from
 * @param animationSpec the [FiniteAnimationSpec] to use for the morphing animation
 */
@Composable
fun rememberInteractionAnimatedShape(
    shapes: InteractionShapes,
    interactionSource: InteractionSource,
    animationSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.fastSpatialSpec(),
): Shape {
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isDragging by interactionSource.collectIsDraggedAsState()

    val currentShape =
        shapes.shapeForInteraction(
            pressed = isPressed,
            focused = isFocused,
            hovered = isHovered,
            dragged = isDragging,
        )

    return rememberAnimatedShape(currentShape, animationSpec)
}
