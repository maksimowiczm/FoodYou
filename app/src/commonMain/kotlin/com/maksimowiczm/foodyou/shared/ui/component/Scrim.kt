package com.maksimowiczm.foodyou.shared.ui.component

import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.util.lerp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.launch

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

@Composable
fun ScrimWithPredictiveBack(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val motionScheme = MaterialTheme.motionScheme
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    val scrimAnimatable = remember { Animatable(if (visible) 1f else 0f) }
    LaunchedEffect(visible) {
        if (visible) scrimAnimatable.animateTo(1f, motionScheme.defaultEffectsSpec())
        else scrimAnimatable.animateTo(0f, motionScheme.defaultEffectsSpec())
    }

    val navigationState = rememberNavigationEventState(NavigationEventInfo.None)
    // TODO
    //  This causes many recompositions, but we need this for back animation to start from the
    //  back progress
    val latestEvent =
        (navigationState.transitionState as? NavigationEventTransitionState.InProgress)?.latestEvent
    NavigationBackHandler(
        isBackEnabled = visible,
        state = navigationState,
        onBackCancelled = {
            scope.launch {
                if (latestEvent != null) scrimAnimatable.snapTo(1 - latestEvent.progress)
                scrimAnimatable.animateTo(1f, motionScheme.fastEffectsSpec())
            }
        },
        onBackCompleted = {
            scope.launch {
                if (latestEvent != null) scrimAnimatable.snapTo(1 - latestEvent.progress)
                scrimAnimatable.animateTo(0f, motionScheme.fastEffectsSpec())
            }
            onDismiss()
        },
    )

    if (scrimAnimatable.value != 0f) {
        Box(modifier) {
            Spacer(
                Modifier.graphicsLayer {
                        val progress = latestEvent?.progress ?: (1 - scrimAnimatable.value)

                        alpha =
                            lerp(
                                .5f,
                                0f,
                                progress,
                            )
                    }
                    .background(MaterialTheme.colorScheme.scrim)
                    .matchParentSize()
                    .pointerInput(Unit) { detectTapGestures { currentOnDismiss() } }
            )
        }
    }
}
