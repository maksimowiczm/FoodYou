package com.maksimowiczm.foodyou.feature.food.data.network.usda

import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.food.data.network.multiplierForUnit
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.RemoteNutritionFacts
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.usda.model.AbridgedFoodItem
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient

internal class USDAProductMapper {
    fun toRemoteProduct(abridgedFoodItem: AbridgedFoodItem) = with(abridgedFoodItem) {
        val proteins = getNutrient(Nutrient.PROTEIN)?.amount ?: 0.0
        val carbohydrates = getNutrient(Nutrient.CARBOHYDRATE)?.amount ?: 0.0
        val fats = getNutrient(Nutrient.FAT)?.amount ?: 0.0

        val calories = getNutrient(Nutrient.CALORIES)?.amount
            ?: getNutrient(Nutrient.CALORIES_ALTERNATIVE)?.amount
            ?: NutrientsHelper.calculateEnergy(
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats
            )

        RemoteProduct(
            name = description.trim(),
            brand = brand?.trim(),
            barcode = barcode?.trim(),
            nutritionFacts = RemoteNutritionFacts(
                proteins = proteins.toFloat(),
                carbohydrates = carbohydrates.toFloat(),
                fats = fats.toFloat(),
                energy = calories.toFloat(),
                saturatedFats = getNutrient(Nutrient.SATURATED_FAT)?.amount?.toFloat(),
                transFats = getNutrient(Nutrient.TRANS_FAT)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "g"))
                }?.toFloat(),
                monounsaturatedFats = getNutrient(Nutrient.MONOUNSATURATED_FAT)?.amount?.toFloat(),
                polyunsaturatedFats = getNutrient(Nutrient.POLYUNSATURATED_FAT)?.amount?.toFloat(),
                omega3 = null,
                omega6 = null,
                sugars = getNutrient(Nutrient.SUGARS)?.amount?.toFloat(),
                addedSugars = getNutrient(Nutrient.ADDED_SUGARS)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "g"))
                }?.toFloat(),
                salt = null,
                fiber = getNutrient(Nutrient.FIBER)?.amount?.toFloat(),
                solubleFiber = null,
                insolubleFiber = null,
                cholesterolMilli = getNutrient(Nutrient.CHOLESTEROL)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                caffeineMilli = getNutrient(Nutrient.CAFFEINE)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                vitaminAMicro = getNutrient(Nutrient.VITAMIN_A)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mcg"))
                }?.toFloat(),
                vitaminB1Milli = getNutrient(Nutrient.VITAMIN_B1)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                vitaminB2Milli = getNutrient(Nutrient.VITAMIN_B2)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                vitaminB3Milli = getNutrient(Nutrient.VITAMIN_B3)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                vitaminB5Milli = getNutrient(Nutrient.VITAMIN_B5)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                vitaminB6Milli = getNutrient(Nutrient.VITAMIN_B6)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                vitaminB7Micro = null,
                vitaminB9Micro = getNutrient(Nutrient.VITAMIN_B9)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mcg"))
                }?.toFloat(),
                vitaminB12Micro = getNutrient(Nutrient.VITAMIN_B12)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mcg"))
                }?.toFloat(),
                vitaminCMilli = getNutrient(Nutrient.VITAMIN_C)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                vitaminDMicro = getNutrient(Nutrient.VITAMIN_D)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mcg"))
                }?.toFloat(),
                vitaminEMilli = getNutrient(Nutrient.VITAMIN_E)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                vitaminKMicro = getNutrient(Nutrient.VITAMIN_K)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mcg"))
                }?.toFloat(),
                manganeseMilli = getNutrient(Nutrient.MANGANESE)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                magnesiumMilli = getNutrient(Nutrient.MAGNESIUM)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                potassiumMilli = getNutrient(Nutrient.POTASSIUM)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                calciumMilli = getNutrient(Nutrient.CALCIUM)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                copperMilli = getNutrient(Nutrient.COPPER)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                zincMilli = getNutrient(Nutrient.ZINC)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                sodiumMilli = getNutrient(Nutrient.SODIUM)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                ironMilli = getNutrient(Nutrient.IRON)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                phosphorusMilli = getNutrient(Nutrient.PHOSPHORUS)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mg"))
                }?.toFloat(),
                seleniumMicro = getNutrient(Nutrient.SELENIUM)?.let {
                    it.amount.times(multiplierForUnit(it.unit, "mcg"))
                }?.toFloat(),
                iodineMicro = null,
                chromiumMicro = null
            ),
            packageWeight = null,
            servingWeight = null,
            source = FoodSource(
                type = FoodSource.Type.USDA,
                url = null
            )
        )
    }
}
