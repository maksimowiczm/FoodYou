package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct

val OpenFoodFactsProduct.headline: String
    get() = if (brand.isNullOrBlank()) {
        name
    } else {
        "$name ($brand)"
    }

val OpenFoodFactsProduct.domainFacts: NutritionFacts?
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
