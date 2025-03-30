package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.model.AddFoodSearchListItem
import com.maksimowiczm.foodyou.feature.diary.ui.component.SearchModelListItem
import com.maksimowiczm.foodyou.feature.diary.ui.component.SearchModelListItemSkeleton
import com.maksimowiczm.foodyou.ui.component.ToggleButton
import com.maksimowiczm.foodyou.ui.component.ToggleButtonDefaults
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun AddFoodSearchListItem.AddFoodSearchListItem(
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isChecked) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isChecked) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    SearchModelListItem(
        name = name,
        brand = brand,
        calories = calories,
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        weight = weight,
        measurement = weightMeasurement,
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = contentColor,
            overlineColor = contentColor,
            supportingColor = contentColor,
            trailingIconColor = contentColor
        ),
        onClick = onClick,
        trailingContent = {
            ToggleButton(
                checked = measurementId != null,
                onCheckChange = onToggle,
                colors = ToggleButtonDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.tertiaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    uncheckedColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                indication = LocalIndication.current
            ) {
                if (measurementId != null) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Composable
fun AddFoodSearchListItemSkeleton(
    modifier: Modifier = Modifier,
    shimmer: Shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )
) {
    SearchModelListItemSkeleton(
        shimmer = shimmer,
        modifier = modifier,
        trailingContent = {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .size(24.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        }
    )
}
