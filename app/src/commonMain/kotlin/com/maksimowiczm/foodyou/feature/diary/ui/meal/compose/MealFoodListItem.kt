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
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummary
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientsRow
import com.maksimowiczm.foodyou.feature.diary.ui.meal.model.MealFoodListItem
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

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

                MeasurementSummary(
                    measurementString = measurementString,
                    measurementStringShort = measurementStringShort,
                    caloriesString = caloriesString,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

private val MealFoodListItem.measurementStringShort: String
    @Composable get() = when (val measurement = weightMeasurement) {
        is WeightMeasurement.Package -> stringResource(
            Res.string.x_times_y,
            measurement.quantity.formatClipZeros(),
            stringResource(Res.string.product_package)
        )

        is WeightMeasurement.Serving -> stringResource(
            Res.string.x_times_y,
            measurement.quantity.formatClipZeros(),
            stringResource(Res.string.product_serving)
        )

        is WeightMeasurement.WeightUnit -> measurement.weight.formatClipZeros(".2f") + " " +
            stringResource(Res.string.unit_gram_short)
    }

private val MealFoodListItem.measurementString: String
    @Composable get() {
        val short = measurementStringShort

        if (weightMeasurement is WeightMeasurement.WeightUnit) {
            return short
        }

        val grams = weightMeasurement.weight

        return "$short (${grams.formatClipZeros()} ${stringResource(Res.string.unit_gram_short)})"
    }

private val MealFoodListItem.caloriesString: String
    @Composable get() = "$calories " + stringResource(Res.string.unit_kcal)
