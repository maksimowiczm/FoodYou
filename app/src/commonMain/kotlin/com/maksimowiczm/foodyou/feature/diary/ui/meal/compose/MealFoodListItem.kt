package com.maksimowiczm.foodyou.feature.diary.ui.meal.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummary
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults.measurementString
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults.measurementStringShort
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientsRow
import com.maksimowiczm.foodyou.feature.diary.ui.meal.model.MealFoodListItem

@Composable
fun MealFoodListItem.MealFoodListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ListItem(
        headlineContent = { Text(name) },
        modifier = modifier then if (onClick !=
            null
        ) {
            Modifier.clickable { onClick() }
        } else {
            Modifier
        },
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
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}
