package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.component.MeasurementSummary
import com.maksimowiczm.foodyou.core.ui.component.NutrientsRow
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.product_package
import foodyou.app.generated.resources.product_serving
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import foodyou.app.generated.resources.x_times_y
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun Ingredient.ListItem(modifier: Modifier = Modifier) {
    val weight = weight
    val measurementString = measurementString
    val caloriesString = caloriesString
    if (weight == null || measurementString == null || caloriesString == null) {
        // TODO handle broken weight
        return
    }

    val proteins = product.nutritionFacts.proteins.value
    val carbohydrates = product.nutritionFacts.carbohydrates.value
    val fats = product.nutritionFacts.fats.value

    ListItem(
        headlineContent = { Text(product.headline) },
        modifier = modifier,
        supportingContent = {
            Column {
                val proteins = (proteins * weight / 100f).roundToInt()
                val carbohydrates = (carbohydrates * weight / 100f).roundToInt()
                val fats = (fats * weight / 100f).roundToInt()

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
                "$short ($weight ${
                    stringResource(
                        Res.string.unit_gram_short
                    )
                })"
        }
    }

private val Ingredient.caloriesString: String?
    @Composable get() = weight?.let {
        val value = (it * product.nutritionFacts.calories.value / 100).roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    }
