package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Scrim(visible: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val scrimAlpha by
        animateFloatAsState(
            targetValue = if (visible) .5f else 0f,
            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        )

    val showScrim by remember { derivedStateOf { scrimAlpha != 0f } }

    if (showScrim) {
        Box(modifier) {
            Spacer(
                Modifier.graphicsLayer { alpha = scrimAlpha }
                    .background(MaterialTheme.colorScheme.scrim)
                    .matchParentSize()
                    .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
            )
        }
    }
}
