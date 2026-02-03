package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.animation.core.Animatable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

@Composable
internal fun FavoriteIcon(
    favorite: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    val scope = rememberCoroutineScope()
    val animatable = remember { Animatable(1f) }
    val motionScheme = MaterialTheme.motionScheme

    val vector = if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
    val tint = if (favorite) MaterialTheme.colorScheme.primary else LocalContentColor.current

    IconButton(
        onClick = {
            onChange(!favorite)
            if (!favorite) {
                scope.launch {
                    animatable.animateTo(1.25f, motionScheme.fastSpatialSpec())
                    animatable.animateTo(1f, motionScheme.slowSpatialSpec())
                }
            }
        },
        shapes = IconButtonDefaults.shapes(),
        modifier = modifier,
    ) {
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
