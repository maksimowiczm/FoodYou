package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.model.AddFoodSearchListItem
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummary
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults.measurementString
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults.measurementStringShort
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientsRow
import com.maksimowiczm.foodyou.ui.component.ToggleButton
import com.maksimowiczm.foodyou.ui.component.ToggleButtonDefaults
import com.maksimowiczm.foodyou.ui.ext.toDp
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

    ListItem(
        headlineContent = { Text(name) },
        modifier = modifier.clickable { onClick() },
        overlineContent = { brand?.let { Text(it) } },
        supportingContent = {
            Column {
                NutrientsRow(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats,
                    modifier = Modifier.fillMaxWidth()
                )

                with(weightMeasurement) {
                    MeasurementSummary(
                        measurementString = measurementString(weight),
                        measurementStringShort = measurementStringShort,
                        caloriesString = MeasurementSummaryDefaults.caloriesString(calories),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
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
        },
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = contentColor,
            overlineColor = contentColor,
            supportingColor = contentColor,
            trailingIconColor = contentColor
        )
    )
}

@Composable
fun AddFoodSearchListItemSkeleton(
    modifier: Modifier = Modifier,
    shimmer: Shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )
) {
    ListItem(
        headlineContent = {
            Column {
                Spacer(Modifier.height(2.dp))
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp() - 4.dp)
                        .width(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Spacer(Modifier.height(2.dp))
            }
        },
        modifier = modifier,
        overlineContent = {
            Spacer(
                Modifier
                    .shimmer(shimmer)
                    .height(LocalTextStyle.current.toDp())
                    .width(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        },
        supportingContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp())
                        .width(125.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp())
                        .width(75.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        },
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
