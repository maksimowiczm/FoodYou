package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlin.math.abs
import kotlin.math.min
import kotlinx.coroutines.launch

@Composable
fun ModalSideSheet(
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SideSheetState = rememberSideSheetState(),
    sheetContent: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    val directionMultiplier =
        when (direction) {
            LayoutDirection.Ltr -> -1
            LayoutDirection.Rtl -> 1
        }

    val predictiveBackProgress = remember { Animatable(0f) }
    val navState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationBackHandler(
        state = navState,
        isBackEnabled = sheetState.progress > 0f,
        onBackCancelled = { scope.launch { predictiveBackProgress.snapTo(0f) } },
        onBackCompleted = {
            scope.launch {
                sheetState.close()
                predictiveBackProgress.snapTo(0f)
            }
        },
    )
    LaunchedEffect(navState.transitionState) {
        val transitionState = navState.transitionState
        if (transitionState is NavigationEventTransitionState.InProgress) {
            val event = transitionState.latestEvent
            when (event.swipeEdge) {
                NavigationEvent.EDGE_LEFT -> predictiveBackProgress.snapTo(event.progress)
                NavigationEvent.EDGE_RIGHT -> predictiveBackProgress.snapTo(-event.progress)
            }
        }
    }

    val maxWidthPx =
        min(
            with(density) { 400.dp.roundToPx() },
            (LocalWindowInfo.current.containerSize.width * 0.9f).fastRoundToInt(),
        )

    Box(modifier) {
        content()

        if (sheetState.progress > 0f) {
            Spacer(
                Modifier.fillMaxSize()
                    .graphicsLayer { alpha = lerp(0f, .5f, sheetState.progress) }
                    .pointerInput(Unit) { detectTapGestures { onDismissRequest() } }
                    .background(MaterialTheme.colorScheme.scrim)
            )
        }

        Surface(
            Modifier.align(Alignment.CenterEnd)
                .fillMaxHeight(1f - abs(predictiveBackProgress.value) * .05f)
                .widthIn(max = with(density) { (maxWidthPx + 100.dp.roundToPx()).toDp() })
                .offset { IntOffset(x = maxWidthPx + 100.dp.roundToPx(), y = 0) }
                .graphicsLayer {
                    val backEffect =
                        lerp(0f, 0.025f, abs(predictiveBackProgress.value)) *
                            -directionMultiplier *
                            if (predictiveBackProgress.value < 0f) 1f else -1f

                    translationX =
                        lerp(
                            0f,
                            directionMultiplier * maxWidthPx.toFloat(),
                            sheetState.progress + backEffect,
                        )

                    clip = true
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                }
        ) {
            Box(Modifier.padding(end = 100.dp)) { sheetContent() }
        }
    }
}

@Composable
fun rememberSideSheetState(isOpen: Boolean = false): SideSheetState {
    var isOpen by rememberSaveable(isOpen) { mutableStateOf(isOpen) }
    val animatable = remember { Animatable(if (isOpen) 1f else 0f) }

    LaunchedEffect(animatable.value) { isOpen = animatable.value != 0f }

    val openSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
    val closeSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()

    return remember(animatable, openSpec, closeSpec) {
        SideSheetState(animatable, openSpec, closeSpec)
    }
}

class SideSheetState(
    private val animatable: Animatable<Float, AnimationVector1D>,
    private val openSpec: AnimationSpec<Float>,
    private val closeSpec: AnimationSpec<Float>,
) {
    val progress by derivedStateOf { animatable.value }

    suspend fun open(animationSpec: AnimationSpec<Float> = closeSpec) {
        animatable.animateTo(1f, animationSpec)
    }

    suspend fun close(animationSpec: AnimationSpec<Float> = openSpec) {
        animatable.animateTo(0f, animationSpec)
    }
}
