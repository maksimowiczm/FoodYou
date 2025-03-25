package com.maksimowiczm.foodyou.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionButtonWithActions(
    expanded: Boolean,
    actions: List<@Composable ColumnScope.() -> Unit>,
    fab: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.End
    ) {
        actions.forEachIndexed { i, action ->
            val i = actions.size - i

            AnimatedVisibility(
                visible = expanded,
                enter = scaleIn(
                    animationSpec = tween(
                        durationMillis = 200,
                        delayMillis = 50 * i
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 200,
                        delayMillis = 50 * i
                    )
                ),
                exit = scaleOut(
                    targetScale = .25f,
                    animationSpec = tween(
                        durationMillis = 200
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 200
                    )
                ) + slideOut {
                    IntOffset(it.width / 5, it.height / 5)
                }
            ) {
                action()
            }
        }

        fab()
    }
}
