package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.maksimowiczm.foodyou.app.ui.common.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalNutrientsOrder
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun FoodListItemSkeleton(
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = LocalContentColor.current,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val order = LocalNutrientsOrder.current

    val headlineContent =
        @Composable {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.titleMediumEmphasized
            ) {
                val extraWidth = rememberSaveable { ((0..100).random().toFloat() / 100f) }

                Spacer(
                    Modifier.shimmer(shimmer)
                        .height(MaterialTheme.typography.titleMediumEmphasized.toDp() - 4.dp)
                        .width(200.dp + extraWidth * 50.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        }

    val supportingContent =
        @Composable {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Spacer(
                            Modifier.shimmer(shimmer)
                                .height(MaterialTheme.typography.bodyMedium.toDp())
                                .width(50.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                        order.forEach { field ->
                            when (field) {
                                NutrientsOrder.Proteins ->
                                    Spacer(
                                        Modifier.shimmer(shimmer)
                                            .height(MaterialTheme.typography.bodyMedium.toDp())
                                            .width(40.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(nutrientsPalette.proteinsOnSurfaceContainer)
                                    )

                                NutrientsOrder.Fats ->
                                    Spacer(
                                        Modifier.shimmer(shimmer)
                                            .height(MaterialTheme.typography.bodyMedium.toDp())
                                            .width(40.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(nutrientsPalette.fatsOnSurfaceContainer)
                                    )

                                NutrientsOrder.Carbohydrates ->
                                    Spacer(
                                        Modifier.shimmer(shimmer)
                                            .height(MaterialTheme.typography.bodyMedium.toDp())
                                            .width(40.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(
                                                nutrientsPalette.carbohydratesOnSurfaceContainer
                                            )
                                    )

                                NutrientsOrder.Other,
                                NutrientsOrder.Vitamins,
                                NutrientsOrder.Minerals -> Unit
                            }
                        }
                    }
                }
            }

            Spacer(
                Modifier.shimmer(shimmer)
                    .height(MaterialTheme.typography.bodyMedium.toDp())
                    .width(125.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }

    val content =
        @Composable {
            Row(
                modifier = Modifier.padding(contentPadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Spacer(
                    Modifier.shimmer(shimmer)
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    headlineContent()
                    supportingContent()
                }
            }
        }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            content = content,
        )
    } else {
        Surface(
            modifier = modifier,
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            content = content,
        )
    }
}

@Preview
@Composable
private fun FoodListItemSkeletonPreview() {
    PreviewFoodYouTheme { FoodListItemSkeleton(rememberShimmer(ShimmerBounds.View)) }
}
