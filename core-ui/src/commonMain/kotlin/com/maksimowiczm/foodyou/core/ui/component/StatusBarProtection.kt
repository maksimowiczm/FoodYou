package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun StatusBarProtection(
    color: Color = StatusBarProtectionDefaults.color,
    height: Dp = StatusBarProtectionDefaults.height()
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        drawRect(
            color = color,
            size = Size(size.width, size.height)
        )
    }
}

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun StatusBarProtection(
    brush: DrawScope.() -> Brush = StatusBarProtectionDefaults.brush,
    height: Dp = StatusBarProtectionDefaults.height()
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        drawRect(
            brush = brush(),
            size = Size(size.width, size.height)
        )
    }
}

object StatusBarProtectionDefaults {
    val brush: DrawScope.() -> Brush
        @Composable
        get() {
            val color = color

            return {
                Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 1f),
                        color.copy(alpha = .9f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = size.height
                )
            }
        }

    val color: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceContainer

    @Composable
    fun height(): Dp = with(LocalDensity.current) {
        return@with WindowInsets.statusBars.getTop(this).toDp()
    }
}
