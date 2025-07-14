package com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.fooddiary.domain.domainFacts
import com.maksimowiczm.foodyou.feature.fooddiary.domain.headline
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
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
            val proteins = measurementFacts.proteins.value
            if (proteins != null) {
                Text("${proteins.formatClipZeros()} $g")
            }
        },
        carbohydrates = {
            val carbohydrates = measurementFacts.carbohydrates.value
            if (carbohydrates != null) {
                Text("${carbohydrates.formatClipZeros()} $g")
            }
        },
        fats = {
            val fats = measurementFacts.fats.value
            if (fats != null) {
                Text("${fats.formatClipZeros()} $g")
            }
        },
        calories = {
            val energy = measurementFacts.energy.value
            val kcal = stringResource(Res.string.unit_kcal)
            if (energy != null) {
                Text("${energy.formatClipZeros("%.0f")} $kcal")
            }
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
