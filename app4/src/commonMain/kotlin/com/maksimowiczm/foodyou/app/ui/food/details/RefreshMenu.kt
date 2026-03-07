package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RefreshMenu(onRefresh: () -> Unit, modifier: Modifier = Modifier.Companion) {
    val animatable = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    IconButton(
        onClick = {
            scope.launch {
                animatable.snapTo(0f)
                animatable.animateTo(360f, tween(500))
            }
            onRefresh()
        },
        shapes = IconButtonDefaults.shapes(),
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Outlined.Refresh,
            contentDescription = stringResource(Res.string.action_refresh),
            modifier = Modifier.graphicsLayer { rotationZ = animatable.value },
        )
    }
}
