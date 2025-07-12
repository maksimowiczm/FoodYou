package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.fooddiary.domain.FoodWithMeasurement
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodErrorListItem
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodListItem
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun MealFoodListItem(
    foodWithMeasurement: FoodWithMeasurement,
    color: Color,
    contentColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    val g = stringResource(Res.string.unit_gram_short)

    val proteinsString = foodWithMeasurement.proteins?.let {
        it.formatClipZeros("%.1f") + " $g"
    }

    val carbohydratesString = foodWithMeasurement.carbohydrates?.let {
        it.formatClipZeros("%.1f") + " $g"
    }

    val fatsString = foodWithMeasurement.fats?.let {
        it.formatClipZeros("%.1f") + " $g"
    }

    val caloriesString = foodWithMeasurement.caloriesString
    val measurementString = foodWithMeasurement.measurementString

    if (
        proteinsString == null ||
        carbohydratesString == null ||
        fatsString == null ||
        caloriesString == null ||
        measurementString == null
    ) {
        FoodErrorListItem(
            headline = foodWithMeasurement.food.headline,
            modifier = modifier
        )
    } else {
        FoodListItem(
            name = { Text(foodWithMeasurement.food.headline) },
            proteins = {
                Text(
                    text = proteinsString,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            carbohydrates = {
                Text(
                    text = carbohydratesString,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            fats = {
                Text(
                    text = fatsString,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            calories = {
                Text(
                    text = caloriesString,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            measurement = {
                Text(
                    text = measurementString,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            modifier = modifier,
            containerColor = color,
            contentColor = contentColor,
            shape = shape,
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}

private val FoodWithMeasurement.measurementStringShort: String
    @Composable get() = with(measurement) {
        when (this) {
            is Measurement.Package -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_package)
            )

            is Measurement.Serving -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_serving)
            )

            is Measurement.Gram -> "${value.formatClipZeros()} " +
                stringResource(Res.string.unit_gram_short)

            is Measurement.Milliliter -> "${value.formatClipZeros()} " +
                stringResource(Res.string.unit_milliliter_short)
        }
    }

private val FoodWithMeasurement.measurementString: String?
    @Composable get() {
        val short = measurementStringShort
        val weight = weight?.formatClipZeros() ?: return null
        val suffix = stringResource(Res.string.unit_gram_short)

        return when (measurement) {
            is Measurement.Gram,
            is Measurement.Milliliter -> short

            is Measurement.Package,
            is Measurement.Serving ->
                "$short ($weight $suffix)"
        }
    }

private val FoodWithMeasurement.caloriesString: String?
    @Composable get() = weight?.let {
        val calories = food.nutritionFacts.energy * it / 100f
        val value = calories.value.roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    }
