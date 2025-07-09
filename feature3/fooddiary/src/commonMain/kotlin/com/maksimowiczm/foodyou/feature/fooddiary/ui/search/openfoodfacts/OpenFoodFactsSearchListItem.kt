package com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts
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

private val OpenFoodFactsProduct.headline: String
    get() = if (brand.isNullOrBlank()) {
        name
    } else {
        "$name ($brand)"
    }

private val OpenFoodFactsProduct.domainFacts: NutritionFacts?
    get() {
        val nutritionFacts = this.nutritionFacts
        if (nutritionFacts == null) return null

        val proteins = nutritionFacts.proteins ?: return null
        val carbohydrates = nutritionFacts.carbohydrates ?: return null
        val fats = nutritionFacts.fats ?: return null
        val energy = nutritionFacts.calories ?: NutrientsHelper.calculateEnergy(
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats
        )

        return NutritionFacts(
            proteins = proteins.toNutrientValue(),
            carbohydrates = carbohydrates.toNutrientValue(),
            energy = energy.toNutrientValue(),
            fats = fats.toNutrientValue(),
            saturatedFats = nutritionFacts.saturatedFats.toNutrientValue(),
            transFats = null.toNutrientValue(),
            monounsaturatedFats = null.toNutrientValue(),
            polyunsaturatedFats = null.toNutrientValue(),
            omega3 = null.toNutrientValue(),
            omega6 = null.toNutrientValue(),
            sugars = nutritionFacts.sugars.toNutrientValue(),
            addedSugars = null.toNutrientValue(),
            dietaryFiber = nutritionFacts.fiber.toNutrientValue(),
            solubleFiber = null.toNutrientValue(),
            insolubleFiber = null.toNutrientValue(),
            salt = nutritionFacts.salt.toNutrientValue(),
            cholesterolMilli = null.toNutrientValue(),
            caffeineMilli = null.toNutrientValue(),
            vitaminAMicro = nutritionFacts.vitaminA.toNutrientValue(),
            vitaminB1Milli = nutritionFacts.vitaminB1.toNutrientValue(),
            vitaminB2Milli = nutritionFacts.vitaminB2.toNutrientValue(),
            vitaminB3Milli = nutritionFacts.vitaminB3.toNutrientValue(),
            vitaminB5Milli = nutritionFacts.vitaminB5.toNutrientValue(),
            vitaminB6Milli = nutritionFacts.vitaminB6.toNutrientValue(),
            vitaminB7Micro = nutritionFacts.vitaminB7.toNutrientValue(),
            vitaminB9Micro = nutritionFacts.vitaminB9.toNutrientValue(),
            vitaminB12Micro = nutritionFacts.vitaminB12.toNutrientValue(),
            vitaminCMilli = nutritionFacts.vitaminC.toNutrientValue(),
            vitaminDMicro = nutritionFacts.vitaminD.toNutrientValue(),
            vitaminEMilli = nutritionFacts.vitaminE.toNutrientValue(),
            vitaminKMicro = nutritionFacts.vitaminK.toNutrientValue(),
            manganeseMilli = nutritionFacts.manganese.toNutrientValue(),
            magnesiumMilli = nutritionFacts.magnesium.toNutrientValue(),
            potassiumMilli = nutritionFacts.potassium.toNutrientValue(),
            calciumMilli = nutritionFacts.calcium.toNutrientValue(),
            copperMilli = nutritionFacts.copper.toNutrientValue(),
            zincMilli = nutritionFacts.zinc.toNutrientValue(),
            sodiumMilli = nutritionFacts.sodium.toNutrientValue(),
            ironMilli = nutritionFacts.iron.toNutrientValue(),
            phosphorusMilli = null.toNutrientValue(),
            seleniumMicro = null.toNutrientValue(),
            iodineMicro = null.toNutrientValue(),
            chromiumMicro = null.toNutrientValue()
        )
    }
