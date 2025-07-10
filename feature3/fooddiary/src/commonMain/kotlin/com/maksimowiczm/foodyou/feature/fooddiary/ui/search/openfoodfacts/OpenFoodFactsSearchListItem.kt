package com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.fooddiary.domain.domainFacts
import com.maksimowiczm.foodyou.feature.fooddiary.domain.headline
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodErrorListItem
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodListItem
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OpenFoodFactsSearchListItem(
    product: OpenFoodFactsProduct,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val facts = product.domainFacts

    if (facts == null) {
        FoodErrorListItem(
            headline = product.headline,
            modifier = modifier,
            errorMessage = stringResource(Res.string.neutral_action_required),
            onClick = onClick
        )
    } else {
        val g = stringResource(Res.string.unit_gram_short)

        val measurement = product.defaultMeasurement

        val factor = when (measurement) {
            is Measurement.Gram,
            is Measurement.Milliliter -> 1f

            is Measurement.Package -> product.packageWeight!! / 100f
            is Measurement.Serving -> product.servingWeight!! / 100f
        }

        val measurementFacts = facts * factor

        FoodListItem(
            name = {
                Text(text = product.headline)
            },
            proteins = {
                val text = measurementFacts.proteins.value.formatClipZeros()
                Text("$text $g")
            },
            carbohydrates = {
                val text = measurementFacts.carbohydrates.value.formatClipZeros()
                Text("$text $g")
            },
            fats = {
                val text = measurementFacts.fats.value.formatClipZeros()
                Text("$text $g")
            },
            calories = {
                val kcal = stringResource(Res.string.unit_kcal)
                val text = measurementFacts.energy.value.formatClipZeros("%.0f")
                Text("$text $kcal")
            },
            measurement = {
                val g = stringResource(Res.string.unit_gram_short)
                val text = buildString {
                    append(measurement.stringResource())
                    val weight = product.weight(measurement)

                    if (weight != null) {
                        append(" (${weight.formatClipZeros()} $g)")
                    }
                }

                Text(text)
            },
            modifier = modifier,
            onClick = onClick
        )
    }
}

private val OpenFoodFactsProduct.defaultMeasurement: Measurement
    get() = when {
        servingWeight != null -> Measurement.Serving(1f)
        packageWeight != null -> Measurement.Package(1f)
        else -> Measurement.Gram(100f)
    }

private fun OpenFoodFactsProduct.weight(measurement: Measurement): Float? = when (measurement) {
    is Measurement.Gram,
    is Measurement.Milliliter -> null

    is Measurement.Package -> packageWeight
    is Measurement.Serving -> servingWeight
}
