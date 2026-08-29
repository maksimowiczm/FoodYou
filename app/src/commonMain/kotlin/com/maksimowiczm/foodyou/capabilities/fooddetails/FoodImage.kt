package com.maksimowiczm.foodyou.capabilities.fooddetails

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.shared.ui.component.Image
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun FoodImage(
    image: FileUri?,
    modifier: Modifier = Modifier,
) {
    val imageModifier = modifier.aspectRatio(16f / 9f).clip(MaterialTheme.shapes.large)

    image?.Image(shimmer = rememberShimmer(ShimmerBounds.View), modifier = imageModifier)
}
