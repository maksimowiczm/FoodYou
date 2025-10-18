package com.maksimowiczm.foodyou.app.ui.food

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun FoodImage.Thumbnail(shimmer: Shimmer, modifier: Modifier = Modifier) {
    when (this) {
        is FoodImage.Remote -> ImageFor(shimmer, modifier) { thumbnail }
    }
}

@Composable
fun FoodImage.Image(shimmer: Shimmer, modifier: Modifier = Modifier) {
    when (this) {
        is FoodImage.Remote -> ImageFor(shimmer, modifier) { fullSize }
    }
}

@Composable
private fun FoodImage.Remote.ImageFor(
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
    image: FoodImage.Remote.() -> String?,
) {
    val image = remember(image) { image(this) }

    val painter = rememberAsyncImagePainter(model = image)
    val state = painter.state.collectAsStateWithLifecycle().value

    val color by
        animateColorAsState(
            when (state) {
                AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Success,
                is AsyncImagePainter.State.Loading -> MaterialTheme.colorScheme.surfaceContainer

                is AsyncImagePainter.State.Error -> MaterialTheme.colorScheme.errorContainer
            }
        )
    val contentColor by
        animateColorAsState(
            when (state) {
                AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Success,
                is AsyncImagePainter.State.Loading -> MaterialTheme.colorScheme.onSurface

                is AsyncImagePainter.State.Error -> MaterialTheme.colorScheme.onErrorContainer
            }
        )

    Surface(
        modifier =
            if (state is AsyncImagePainter.State.Loading)
                modifier.shimmer(shimmer).clip(MaterialTheme.shapes.medium)
            else modifier,
        shape = MaterialTheme.shapes.medium,
        color = color,
        contentColor = contentColor,
    ) {
        when (state) {
            AsyncImagePainter.State.Empty -> Unit
            is AsyncImagePainter.State.Error ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Outlined.BrokenImage, contentDescription = null)
                }

            is AsyncImagePainter.State.Loading -> Unit
            is AsyncImagePainter.State.Success ->
                Image(
                    painter = state.painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
        }
    }
}
