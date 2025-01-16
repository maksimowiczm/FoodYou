package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity

@Composable
fun rememberDiaryTopBarScrollBehavior(): DiaryTopBarScrollBehavior {
    val anchoredDraggableState = rememberSaveable(
        saver = AnchoredDraggableState.Saver()
    ) {
        AnchoredDraggableState(
            initialValue = DiaryTopBarScrollBehavior.SizeState.Expanded
        )
    }

    val density = LocalDensity.current
    val anchors = remember(density) {
        val headlineHeight = with(density) { DiaryTopBarDefaults.headlineHeight.toPx() }
        val expandedHeight = with(density) { DiaryTopBarDefaults.maxHeight.toPx() }

        DraggableAnchors {
            DiaryTopBarScrollBehavior.SizeState.Expanded at 0f
            DiaryTopBarScrollBehavior.SizeState.DatePicker at -headlineHeight
            DiaryTopBarScrollBehavior.SizeState.Collapsed at -expandedHeight
        }
    }

    SideEffect {
        anchoredDraggableState.updateAnchors(anchors)
    }

    val scrollBehavior = remember(
        density,
        anchoredDraggableState
    ) {
        DiaryTopBarScrollBehavior(
            density = density,
            anchoredDraggableState = anchoredDraggableState
        )
    }

    return scrollBehavior
}

class DiaryTopBarScrollBehavior(
    private val density: Density,
    val anchoredDraggableState: AnchoredDraggableState<SizeState>
) {
    private var datePickerHeight =
        with(density) { DiaryTopBarDefaults.datePickerMaxHeight.toPx() }

    fun setDatePickerHeight(value: Int) {
        datePickerHeight = value.toFloat()

        val anchors = DraggableAnchors {
            SizeState.Expanded at 0f
            SizeState.DatePicker at -headlineMaxHeight
            SizeState.Collapsed at -headlineMaxHeight - datePickerHeight
        }

        anchoredDraggableState.updateAnchors(anchors)
    }

    enum class SizeState {
        Expanded,
        DatePicker,
        Collapsed
    }

    private val offset: Float
        get() {
            return runCatching {
                anchoredDraggableState.requireOffset()
            }.getOrDefault(0f)
        }

    private val headlineMaxHeight = with(density) { DiaryTopBarDefaults.headlineHeight.toPx() }
    val headlineHeight: Dp by derivedStateOf {
        val pixels = (headlineMaxHeight + offset).coerceIn(
            0f,
            headlineMaxHeight
        )

        with(density) { pixels.toDp() }
    }

    val horizontalPagerMaxHeight: Dp by derivedStateOf {
        val trueOffset = offset + headlineMaxHeight

        val pixels = (datePickerHeight + trueOffset).coerceIn(
            0f,
            datePickerHeight
        )

        with(density) { pixels.toDp() }
    }

    val state by derivedStateOf { anchoredDraggableState.currentValue }

    // TODO
    //  It wiggles on overscroll if it doesn't scroll all the way up.
    /**
     * Top bar will collapse when content is pulled up and will expand when content is pulled all
     * the way down.
     */
    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            if (delta > 0f) {
                return Offset.Zero
            }

            val consumed = anchoredDraggableState.dispatchRawDelta(available.y)
            return Offset(0f, consumed)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (available.y > 0f) {
                val consumedHeight = anchoredDraggableState.dispatchRawDelta(available.y)
                return Offset(0f, consumedHeight)
            }

            return super.onPostScroll(consumed, available, source)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            anchoredDraggableState.settle(
                tween(
                    durationMillis = 100,
                    easing = LinearEasing
                )
            )

            return super.onPostFling(consumed, available)
        }
    }
}
