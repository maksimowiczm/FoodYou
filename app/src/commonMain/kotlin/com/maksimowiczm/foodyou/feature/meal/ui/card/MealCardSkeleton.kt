package com.maksimowiczm.foodyou.feature.meal.ui.card

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
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.feature.meal.ui.component.MealHeader
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer

@Composable
internal fun MealCardSkeleton(shimmer: Shimmer, modifier: Modifier = Modifier.Companion) {
    val headline = @Composable {
        Column {
            Box(
                modifier = Modifier.Companion
                    .shimmer(shimmer)
                    .size(140.dp, MaterialTheme.typography.headlineMedium.toDp() - 4.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
            Spacer(Modifier.Companion.height(4.dp))
        }
    }
    val time = @Composable {
        Box(
            modifier = Modifier.Companion
                .shimmer(shimmer)
                .size(60.dp, MaterialTheme.typography.labelLarge.toDp())
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
    val nutrientsLayout = @Composable {
        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Box(
                modifier = Modifier.Companion
                    .shimmer(shimmer)
                    .size(120.dp, MaterialTheme.typography.labelMedium.toDp() * 2)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.Companion.weight(1f))

            FilledIconButton(
                onClick = {},
                modifier = Modifier.Companion.shimmer(shimmer),
                colors = IconButtonDefaults.filledIconButtonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                enabled = false,
                content = {}
            )
        }
    }

    FoodYouHomeCard(
        modifier = modifier
    ) {
        MealHeader(
            modifier = Modifier.Companion.padding(16.dp),
            headline = headline,
            time = time,
            nutrientsLayout = nutrientsLayout
        )
    }
}
