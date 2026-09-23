package com.maksimowiczm.foodyou.shared.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity

@Composable
fun StatusBarProtection(
    color: Color = MaterialTheme.colorScheme.surface,
    height: Int = WindowInsets.statusBars.getTop(LocalDensity.current),
    progress: () -> Float = { 1f },
) {
    val density = LocalDensity.current
    val dpHeight = density.run { height.toDp() }

    val brush =
        remember(color, height) {
            Brush.verticalGradient(
                0f to color.copy(alpha = .9f),
                0.6f to color.copy(alpha = .8f),
                0.7f to color.copy(alpha = .6f),
                0.9f to color.copy(alpha = .4f),
                1f to Color.Transparent,
            )
        }
    Canvas(Modifier.fillMaxWidth().height(dpHeight)) {
        drawRect(brush = brush, size = Size(size.width, size.height), alpha = progress())
    }
}

object StatusBarProtectionDefaults {

    /**
     * A [NestedScrollConnection] that calls [onUpdate] with the amount of scroll consumed in
     * [NestedScrollConnection.onPostScroll].
     */
    @Composable
    fun rememberScrollConnection(onUpdate: (consumed: Offset) -> Unit): NestedScrollConnection =
        remember {
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
}
