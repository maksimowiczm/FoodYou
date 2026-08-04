package com.maksimowiczm.foodyou.app.ui.food.common

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

interface FoodUiScopeWithImage {
    val image: FileUri?
}

@Composable
fun FoodUiScopeWithImage.Image(modifier: Modifier = Modifier) {
    val image = image
    val imageModifier = modifier.aspectRatio(16f / 9f).clip(MaterialTheme.shapes.large)

    image?.Image(shimmer = rememberShimmer(ShimmerBounds.View), modifier = imageModifier)
}
