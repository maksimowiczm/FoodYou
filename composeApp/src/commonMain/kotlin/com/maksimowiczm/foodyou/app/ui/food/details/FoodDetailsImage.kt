package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
internal fun FoodDetailsImage(
    image: FileUri?,
    showPlaceholder: Boolean,
    modifier: Modifier = Modifier,
) {
    val imageModifier = modifier.aspectRatio(16f / 9f).clip(MaterialTheme.shapes.large)

    when {
        image != null ->
            image.Image(shimmer = rememberShimmer(ShimmerBounds.View), modifier = imageModifier)

        showPlaceholder ->
            Spacer(imageModifier.shimmer().background(MaterialTheme.colorScheme.secondaryContainer))

        else -> Unit
    }
}
