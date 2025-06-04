package com.maksimowiczm.foodyou.feature.measurement.ui.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.max

@Composable
internal fun WeightCaloriesLayout(
    weight: @Composable () -> Unit,
    calories: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Layout(
        content = {
            weight()
            calories()
        },
        modifier = modifier
    ) { (weightMeasurable, caloriesMeasurable), constraints ->
        val weightWidth = weightMeasurable.minIntrinsicWidth(constraints.maxHeight)
        val caloriesWidth = caloriesMeasurable.minIntrinsicWidth(constraints.maxHeight)

        val halfWidth = constraints.maxWidth / 2

        if (weightWidth < halfWidth && caloriesWidth < halfWidth) {
            val weightPlaceable = weightMeasurable.measure(
                constraints.copy(
                    minWidth = halfWidth,
                    maxWidth = halfWidth
                )
            )
            val caloriesPlaceable = caloriesMeasurable.measure(
                constraints.copy(
                    minWidth = halfWidth,
                    maxWidth = halfWidth
                )
            )
            val height = max(weightPlaceable.height, caloriesPlaceable.height)

            layout(constraints.maxWidth, height) {
                weightPlaceable.placeRelative(0, (height - weightPlaceable.height) / 2)
                caloriesPlaceable.placeRelative(
                    halfWidth,
                    (height - caloriesPlaceable.height) / 2
                )
            }
        } else {
            val caloriesPlaceable = caloriesMeasurable.measure(constraints)

            layout(constraints.maxWidth, caloriesPlaceable.height) {
                caloriesPlaceable.placeRelative(0, 0)
            }
        }
    }
}
