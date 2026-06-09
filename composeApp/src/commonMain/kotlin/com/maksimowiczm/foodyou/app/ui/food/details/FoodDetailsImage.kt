package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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
    val imageModifier =
        modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .padding(horizontal = 32.dp)
            .clip(MaterialTheme.shapes.medium)

    when {
        image != null ->
            image.Image(shimmer = rememberShimmer(ShimmerBounds.View), modifier = imageModifier)

        showPlaceholder ->
            Spacer(imageModifier.shimmer().background(MaterialTheme.colorScheme.secondaryContainer))

        else -> Unit
    }
}
