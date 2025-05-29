package com.maksimowiczm.foodyou.feature.reciperedesign.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.component.FoodErrorListItem
import com.maksimowiczm.foodyou.core.ui.component.FoodListItem
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.product_package
import foodyou.app.generated.resources.product_serving
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import foodyou.app.generated.resources.x_times_y
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
fun IngredientListItem(ingredient: Ingredient, modifier: Modifier = Modifier) {
    val weight = ingredient.weight
    val proteins = ingredient.proteins
    val carbohydrates = ingredient.carbohydrates
    val fats = ingredient.fats
    val caloriesString = ingredient.caloriesString
    val measurementString = ingredient.measurementString

    if (
        weight == null ||
        proteins == null ||
        carbohydrates == null ||
        fats == null ||
        caloriesString == null ||
        measurementString == null
    ) {
        FoodErrorListItem(
            headline = ingredient.food.headline,
            modifier = modifier
        )
        return
    }

    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        name = { Text(ingredient.food.headline) },
        proteins = {
            val proteins = (proteins * weight / 100f).formatClipZeros("%.1f")
            Text("$proteins $g")
        },
        carbohydrates = {
            val carbohydrates = (carbohydrates * weight / 100f).formatClipZeros("%.1f")
            Text("$carbohydrates $g")
        },
        fats = {
            val fats = (fats * weight / 100f).formatClipZeros("%.1f")
            Text("$fats $g")
        },
        calories = { Text(caloriesString) },
        measurement = { Text(measurementString) },
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}

private val Ingredient.measurementStringShort: String
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
        }
    }

private val Ingredient.measurementString: String?
    @Composable get() {
        val short = measurementStringShort
        val weight = weight?.formatClipZeros() ?: return null

        return when (measurement) {
            is Measurement.Gram -> short
            is Measurement.Package,
            is Measurement.Serving ->
                "$short ($weight ${stringResource(Res.string.unit_gram_short)})"
        }
    }

private val Ingredient.caloriesString: String?
    @Composable get() = weight?.let {
        val value = (it * food.nutritionFacts.calories.value / 100).roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    }
