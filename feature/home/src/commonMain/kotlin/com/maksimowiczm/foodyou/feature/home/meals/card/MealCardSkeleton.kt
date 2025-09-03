package com.maksimowiczm.foodyou.feature.home.meals.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.home.shared.FoodYouHomeCard
import com.maksimowiczm.foodyou.shared.ui.ext.toDp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer

@Composable
internal fun MealCardSkeleton(shimmer: Shimmer, modifier: Modifier = Modifier) {
    FoodYouHomeCard(modifier) {
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier.shimmer(shimmer)
                    .size(140.dp, MaterialTheme.typography.headlineMedium.toDp() - 4.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(4.dp))

            Box(
                Modifier.shimmer(shimmer)
                    .size(60.dp, MaterialTheme.typography.labelLarge.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.shimmer(shimmer)
                        .size(120.dp, MaterialTheme.typography.labelMedium.toDp() * 2)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Spacer(Modifier.weight(1f))
                FilledIconButton(
                    onClick = {},
                    modifier = Modifier.shimmer(shimmer),
                    colors =
                        IconButtonDefaults.filledIconButtonColors(
                            disabledContainerColor =
                                MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                    shape = MaterialTheme.shapes.medium,
                    enabled = false,
                    content = {},
                )
                FilledIconButton(
                    onClick = {},
                    modifier = Modifier.shimmer(shimmer),
                    colors =
                        IconButtonDefaults.filledIconButtonColors(
                            disabledContainerColor =
                                MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                    shape = MaterialTheme.shapes.medium,
                    enabled = false,
                    content = {},
                )
            }
        }
    }
}
