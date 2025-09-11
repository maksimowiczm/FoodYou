package com.maksimowiczm.foodyou.shared.compose.extension

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
context(scope: ReorderableCollectionItemScope)
fun Modifier.hapticDraggableHandle(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onDragStarted: (startedPosition: Offset) -> Unit = {},
    onDragStopped: () -> Unit = {},
) =
    with(scope) {
        val localHapticFeedback = LocalHapticFeedback.current

        draggableHandle(
            enabled = enabled,
            interactionSource = interactionSource,
            onDragStarted = {
                localHapticFeedback.performHapticFeedback(
                    HapticFeedbackType.GestureThresholdActivate
                )
                onDragStarted(it)
            },
            onDragStopped = {
                localHapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                onDragStopped()
            },
        )
    }
