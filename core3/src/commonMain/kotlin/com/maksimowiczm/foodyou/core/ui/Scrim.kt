package com.maksimowiczm.foodyou.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Scrim(visible: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val scrimAlpha by animateFloatAsState(
        targetValue = if (visible) .5f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
    )

    val showScrim by remember {
        derivedStateOf { scrimAlpha != 0f }
    }

    if (showScrim) {
        Box(modifier) {
            Spacer(
                Modifier
                    .graphicsLayer { alpha = scrimAlpha }
                    .background(MaterialTheme.colorScheme.scrim)
                    .matchParentSize()
                    .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
            )
        }
    }
}
