package com.maksimowiczm.foodyou.features.food.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

private const val heartbeatDurationMs = 600
private val heartBeatSpec by
    lazy(LazyThreadSafetyMode.NONE) {
        keyframes {
            durationMillis = heartbeatDurationMs
            1f at 0
            1.30f at (heartbeatDurationMs * 0.24f).toInt() // first beat peak
            0.9f at (heartbeatDurationMs * 0.44f).toInt() // settle
            1.1f at (heartbeatDurationMs * 0.68f).toInt() // second beat peak
            1f at heartbeatDurationMs // rest
        }
    }

@Composable
internal fun FavoriteIconButton(
    favorite: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
) {
    val scope = rememberCoroutineScope()
    val animatable = remember { Animatable(1f) }

    val vector = if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder

    IconButton(
        onClick = {
            onChange(!favorite)
            scope.launch {
                if (favorite) {
                    animatable.snapTo(1f)
                } else {
                    animatable.snapTo(1f)
                    animatable.animateTo(targetValue = 1f, animationSpec = heartBeatSpec)
                }
            }
        },
        enabled = enabled,
        shapes = IconButtonDefaults.shapes(),
        colors = colors,
        modifier = modifier,
    ) {
        val tint = if (favorite) MaterialTheme.colorScheme.primary else LocalContentColor.current

        Icon(
            imageVector = vector,
            contentDescription = null,
            tint = tint,
            modifier =
                Modifier.graphicsLayer {
                    scaleX = animatable.value
                    scaleY = animatable.value
                },
        )
    }
}
