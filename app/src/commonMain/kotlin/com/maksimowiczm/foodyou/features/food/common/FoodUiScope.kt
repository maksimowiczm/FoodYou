package com.maksimowiczm.foodyou.features.food.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.maksimowiczm.foodyou.shared.ui.extension.toDp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

interface FoodUiScope {
    val headline: String?
    val isFavorite: Boolean
}

@Composable
fun FoodUiScope.Headline(
    modifier: Modifier = Modifier,
    shimmer: Shimmer = rememberShimmer(ShimmerBounds.View),
) {
    val headline = headline

    Box(modifier) {
        if (headline != null) {
            Text(text = headline, style = MaterialTheme.typography.displaySmall)
        } else {
            Spacer(
                Modifier.shimmer(shimmer)
                    .fillMaxWidth(.75f)
                    .height(MaterialTheme.typography.displaySmall.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    }
}
