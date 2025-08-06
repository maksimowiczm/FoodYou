package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

fun toEntityNutrients(nutritionFacts: NutritionFacts): Triple<Nutrients, Vitamins, Minerals> {
    val nutrients =
        Nutrients(
            proteins = nutritionFacts.proteins.value,
            carbohydrates = nutritionFacts.carbohydrates.value,
            energy = nutritionFacts.energy.value,
            fats = nutritionFacts.fats.value,
            saturatedFats = nutritionFacts.saturatedFats.value,
            transFats = nutritionFacts.transFats.value,
            monounsaturatedFats = nutritionFacts.monounsaturatedFats.value,
            polyunsaturatedFats = nutritionFacts.polyunsaturatedFats.value,
            omega3 = nutritionFacts.omega3.value,
            omega6 = nutritionFacts.omega6.value,
            sugars = nutritionFacts.sugars.value,
            addedSugars = nutritionFacts.addedSugars.value,
            dietaryFiber = nutritionFacts.dietaryFiber.value,
            solubleFiber = nutritionFacts.solubleFiber.value,
            insolubleFiber = nutritionFacts.insolubleFiber.value,
            salt = nutritionFacts.salt.value,
            cholesterolMilli = nutritionFacts.cholesterol.value?.times(1_000.0),
            caffeineMilli = nutritionFacts.caffeine.value?.times(1_000.0),
        )
    val vitamins =
        Vitamins(
            vitaminAMicro = nutritionFacts.vitaminA.value?.times(1_000_000.0),
            vitaminB1Milli = nutritionFacts.vitaminB1.value?.times(1_000.0),
            vitaminB2Milli = nutritionFacts.vitaminB2.value?.times(1_000.0),
            vitaminB3Milli = nutritionFacts.vitaminB3.value?.times(1_000.0),
            vitaminB5Milli = nutritionFacts.vitaminB5.value?.times(1_000.0),
            vitaminB6Milli = nutritionFacts.vitaminB6.value?.times(1_000.0),
            vitaminB7Micro = nutritionFacts.vitaminB7.value?.times(1_000_000.0),
            vitaminB9Micro = nutritionFacts.vitaminB9.value?.times(1_000_000.0),
            vitaminB12Micro = nutritionFacts.vitaminB12.value?.times(1_000_000.0),
            vitaminCMilli = nutritionFacts.vitaminC.value?.times(1_000.0),
            vitaminDMicro = nutritionFacts.vitaminD.value?.times(1_000_000.0),
            vitaminEMilli = nutritionFacts.vitaminE.value?.times(1_000.0),
            vitaminKMicro = nutritionFacts.vitaminK.value?.times(1_000_000.0),
        )
    val minerals =
        Minerals(
            manganeseMilli = nutritionFacts.manganese.value?.times(1_000.0),
            magnesiumMilli = nutritionFacts.magnesium.value?.times(1_000.0),
            potassiumMilli = nutritionFacts.potassium.value?.times(1_000.0),
            calciumMilli = nutritionFacts.calcium.value?.times(1_000.0),
            copperMilli = nutritionFacts.copper.value?.times(1_000.0),
            zincMilli = nutritionFacts.zinc.value?.times(1_000.0),
            sodiumMilli = nutritionFacts.sodium.value?.times(1_000.0),
            ironMilli = nutritionFacts.iron.value?.times(1_000.0),
            phosphorusMilli = nutritionFacts.phosphorus.value?.times(1_000.0),
            seleniumMicro = nutritionFacts.selenium.value?.times(1_000_000.0),
            iodineMicro = nutritionFacts.iodine.value?.times(1_000_000.0),
            chromiumMicro = nutritionFacts.chromium.value?.times(1_000_000.0),
        )
    return Triple(nutrients, vitamins, minerals)
}
