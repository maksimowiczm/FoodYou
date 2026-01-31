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
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.maksimowiczm.foodyou.common.domain.food.FoodImage
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.coil.securelyAccessFile
import io.github.vinceglb.filekit.lastModified

@Composable
fun FoodImage.Thumbnail(shimmer: Shimmer, modifier: Modifier = Modifier) {
    when (this) {
        is FoodImage.Remote -> ImageFor(shimmer, modifier) { thumbnail }
        is FoodImage.Local -> ImageFor(shimmer, modifier)
    }
}

@Composable
fun FoodImage.Image(shimmer: Shimmer, modifier: Modifier = Modifier) {
    when (this) {
        is FoodImage.Remote -> ImageFor(shimmer, modifier) { fullSize }
        is FoodImage.Local -> ImageFor(shimmer, modifier)
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

@Composable
fun FoodImage.Local.ImageFor(shimmer: Shimmer, modifier: Modifier = Modifier) {
    val platformContext = LocalPlatformContext.current
    val model =
        remember(platformContext, uri) {
            val file = PlatformFile(uri)
            // This is to force Coil to reload the image when the file is modified, it kind of
            // leaks from infrastructure to UI layer
            val cacheKey = "${file.absolutePath()}_${file.lastModified()}"

            ImageRequest.Builder(platformContext)
                .data(uri)
                .memoryCacheKey(cacheKey)
                .diskCacheKey(cacheKey)
                .build()
        }
    val file = remember(uri) { PlatformFile(uri) }
    val painter =
        rememberAsyncImagePainter(model = model, onState = { it.securelyAccessFile(file) })
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
