package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun FoodListItemSkeleton(
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
    headline: @Composable () -> Unit = { FoodListItemSkeletonDefaults.Headline(shimmer) },
    image: @Composable (() -> Unit)? = { FoodListItemSkeletonDefaults.Image(shimmer) },
    proteins: @Composable () -> Unit = { FoodListItemSkeletonDefaults.Nutrient(shimmer) },
    carbohydrates: @Composable () -> Unit = { FoodListItemSkeletonDefaults.Nutrient(shimmer) },
    fats: @Composable () -> Unit = { FoodListItemSkeletonDefaults.Nutrient(shimmer) },
    energy: @Composable () -> Unit = { FoodListItemSkeletonDefaults.Energy(shimmer) },
    quantity: @Composable () -> Unit = { FoodListItemSkeletonDefaults.Quantity(shimmer) },
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = LocalContentColor.current,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    FoodListItem(
        headline = headline,
        image = image,
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        energy = energy,
        quantity = quantity,
        modifier = modifier,
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = shape,
        contentPadding = contentPadding,
    )
}

object FoodListItemSkeletonDefaults {
    @Composable
    fun Headline(shimmer: Shimmer) {
        val extraWidth = rememberSaveable { ((0..100).random().toFloat() / 100f) }
        Spacer(
            Modifier.shimmer(shimmer)
                .height(LocalTextStyle.current.toDp())
                .width(200.dp + extraWidth * 50.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }

    @Composable
    fun Image(shimmer: Shimmer) {
        Spacer(
            Modifier.shimmer(shimmer)
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }

    @Composable
    fun Nutrient(shimmer: Shimmer) {
        val extraWidth = rememberSaveable { ((0..30).random().toFloat() / 100f) }
        Spacer(
            Modifier.shimmer(shimmer)
                .height(LocalTextStyle.current.toDp())
                .width((30 * (1 + extraWidth)).dp)
                .clip(MaterialTheme.shapes.medium)
                .background(LocalContentColor.current)
        )
    }

    @Composable
    fun Energy(shimmer: Shimmer) {
        val extraWidth = rememberSaveable { ((0..30).random().toFloat() / 100f) }
        Spacer(
            Modifier.shimmer(shimmer)
                .height(LocalTextStyle.current.toDp())
                .width((40 * (1 + extraWidth)).dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }

    @Composable
    fun Quantity(shimmer: Shimmer) {
        val extraWidth = rememberSaveable { ((0..50).random().toFloat() / 100f) }
        Spacer(
            Modifier.shimmer(shimmer)
                .height(LocalTextStyle.current.toDp())
                .width((100 * (1 + extraWidth)).dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}

@Preview
@Composable
private fun FoodListItemSkeletonPreview() {
    PreviewFoodYouTheme { FoodListItemSkeleton(rememberShimmer(ShimmerBounds.View)) }
}

@Preview
@Composable
private fun HeadlinedFoodListItemSkeletonPreview() {
    PreviewFoodYouTheme {
        FoodListItemSkeleton(
            headline = { Text("Food name") },
            shimmer = rememberShimmer(ShimmerBounds.View),
        )
    }
}
