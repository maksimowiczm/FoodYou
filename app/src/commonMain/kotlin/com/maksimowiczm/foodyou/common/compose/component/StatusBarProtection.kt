package com.maksimowiczm.foodyou.common.compose.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity

@Composable
fun StatusBarProtection(
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    progress: () -> Float = { 1f },
) {
    val height = WindowInsets.Companion.statusBars.getTop(LocalDensity.current)
    Canvas(Modifier.Companion.fillMaxSize()) {
        drawRect(
            color = color,
            size = Size(size.width, height.toFloat()),
            alpha = progress().coerceIn(0f, 1f),
        )
    }
}

object StatusBarProtectionDefaults {

    /**
     * A [NestedScrollConnection] that calls [onUpdate] with the amount of scroll consumed in
     * [NestedScrollConnection.onPostScroll].
     */
    fun scrollConnection(onUpdate: (consumed: Offset) -> Unit): NestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                onUpdate(consumed)
                return super.onPostScroll(consumed, available, source)
            }
        }
}
