package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults.caloriesString
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults.measurementString
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults.measurementStringShort
import com.maksimowiczm.foodyou.ui.ext.toDp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun SearchModelListItem(
    name: String,
    brand: String?,
    calories: Int,
    proteins: Int,
    carbohydrates: Int,
    fats: Int,
    weight: Float,
    measurement: WeightMeasurement,
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    ListItem(
        headlineContent = { Text(name) },
        modifier = modifier then if (onClick != null) {
            Modifier.clickable { onClick() }
        } else {
            Modifier
        },
        overlineContent = brand?.let { { Text(it) } },
        supportingContent = {
            Column {
                NutrientsRow(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats,
                    modifier = Modifier.fillMaxWidth()
                )

                with(measurement) {
                    MeasurementSummary(
                        measurementString = measurementString(weight),
                        measurementStringShort = measurementStringShort,
                        caloriesString = caloriesString(calories),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        trailingContent = trailingContent,
        colors = colors
    )
}

@Composable
fun SearchModelListItemSkeleton(
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
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
        trailingContent = trailingContent
    )
}
