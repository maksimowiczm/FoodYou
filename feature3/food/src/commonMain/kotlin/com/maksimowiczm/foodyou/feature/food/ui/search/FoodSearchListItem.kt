package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
import com.maksimowiczm.foodyou.feature.food.ui.FoodErrorListItem
import com.maksimowiczm.foodyou.feature.food.ui.FoodListItem
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FoodSearchListItem(
    food: FoodSearch,
    measurement: Measurement,
    onClick: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    val factor = when (measurement) {
        is Measurement.Gram,
        is Measurement.Milliliter -> 1f

        is Measurement.Package -> food.totalWeight?.let { totalWeight ->
            measurement.weight(totalWeight) / 100f
        }

        is Measurement.Serving -> food.servingWeight?.let { servingWeight ->
            measurement.weight(servingWeight) / 100f
        }
    }

    if (factor == null) {
        return FoodErrorListItem(
            headline = food.headline,
            errorMessage = stringResource(Res.string.error_measurement_error),
            modifier = modifier,
            onClick = onClick,
            shape = shape
        )
    }

    val measurementFacts = food.nutritionFacts * factor
    val proteins = measurementFacts.proteins.value
    val carbohydrates = measurementFacts.carbohydrates.value
    val fats = measurementFacts.fats.value
    val energy = measurementFacts.energy.value

    if (proteins == null || carbohydrates == null || fats == null || energy == null) {
        return FoodErrorListItem(
            headline = food.headline,
            modifier = modifier,
            onClick = onClick,
            shape = shape,
            errorMessage = stringResource(Res.string.error_food_is_missing_required_fields)
        )
    }

    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        name = {
            Text(text = food.headline)
        },
        proteins = {
            val text = proteins.formatClipZeros()
            Text("$text $g")
        },
        carbohydrates = {
            val text = carbohydrates.formatClipZeros()
            Text("$text $g")
        },
        fats = {
            val text = fats.formatClipZeros()
            Text("$text $g")
        },
        calories = {
            val kcal = stringResource(Res.string.unit_kcal)
            val text = energy.formatClipZeros("%.0f")
            Text("$text $kcal")
        },
        measurement = {
            val weight = when (measurement) {
                is Measurement.Gram,
                is Measurement.Milliliter -> null

                is Measurement.Package -> food.totalWeight?.let(measurement::weight)
                is Measurement.Serving -> food.servingWeight?.let(measurement::weight)
            }

            val text = buildString {
                append(measurement.stringResource())
                if (weight != null) {
                    append(" (${weight.formatClipZeros()} $g)")
                }
            }

            Text(text)
        },
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = shape
    )
}
