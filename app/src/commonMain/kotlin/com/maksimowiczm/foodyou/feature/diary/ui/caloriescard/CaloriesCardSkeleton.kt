package com.maksimowiczm.foodyou.feature.diary.ui.caloriescard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.ui.ext.toDp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun CaloriesCardSkeleton(
    state: CaloriesCardState,
    onToggleState: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shimmerInstance: Shimmer = rememberShimmer(ShimmerBounds.View)
) {
    CaloriesCardLayout(
        state = state,
        header = {
            CaloriesCardLayoutDefaults.Header(
                state = state,
                onToggleState = onToggleState
            ) {
                Box(
                    modifier = Modifier
                        .shimmer(shimmerInstance)
                        .size(100.dp, MaterialTheme.typography.titleLarge.toDp())
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        },
        compactContent = {
            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(160.dp, MaterialTheme.typography.headlineLarge.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .height(16.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(100.dp, MaterialTheme.typography.labelLarge.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        },
        expandedContent = {
            Spacer(Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NutrientIndicatorSkeleton(shimmerInstance)
                NutrientIndicatorSkeleton(shimmerInstance)
                NutrientIndicatorSkeleton(shimmerInstance)
            }
        },
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun NutrientIndicatorSkeleton(shimmerInstance: Shimmer, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(60.dp, MaterialTheme.typography.titleMedium.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(60.dp, MaterialTheme.typography.headlineSmall.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }

        Box(
            modifier = Modifier
                .shimmer(shimmerInstance)
                .height(4.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}
