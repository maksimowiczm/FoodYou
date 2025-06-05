package com.maksimowiczm.foodyou.feature.product.data.network.usda

import com.maksimowiczm.foodyou.feature.product.data.network.usda.model.AbridgedFoodItem
import com.maksimowiczm.foodyou.feature.product.data.network.usda.model.Nutrient
import com.maksimowiczm.foodyou.feature.product.domain.RemoteNutritionFacts
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct

internal object USDAProductMapper {
    fun toRemoteProduct(abridgedFoodItem: AbridgedFoodItem) = with(abridgedFoodItem) {
        RemoteProduct(
            name = description.trim(),
            brand = brand?.trim(),
            barcode = barcode?.trim(),
            nutritionFacts = RemoteNutritionFacts(
                proteins = (getNutrient(Nutrient.PROTEIN)?.amount ?: 0.0).toFloat(),
                carbohydrates = (getNutrient(Nutrient.CARBOHYDRATE)?.amount ?: 0.0).toFloat(),
                fats = (getNutrient(Nutrient.FAT)?.amount ?: 0.0).toFloat(),
                calories = (getNutrient(Nutrient.CALORIES)?.amount ?: 0.0).toFloat(),
                saturatedFats = getNutrient(Nutrient.SATURATED_FAT)?.amount?.toFloat(),
                monounsaturatedFats = getNutrient(Nutrient.MONOUNSATURATED_FAT)?.amount?.toFloat(),
                polyunsaturatedFats = getNutrient(Nutrient.POLYUNSATURATED_FAT)?.amount?.toFloat(),
                omega3 = null,
                omega6 = null,
                sugars = getNutrient(Nutrient.SUGARS)?.amount?.toFloat(),
                salt = null,
                fiber = getNutrient(Nutrient.FIBER)?.amount?.toFloat(),
                cholesterolMilli = getNutrient(Nutrient.CHOLESTEROL)?.amount?.toFloat(),
                caffeineMilli = getNutrient(Nutrient.CAFFEINE)?.amount?.toFloat(),
                vitaminAMicro = null,
                vitaminB1Milli = getNutrient(Nutrient.VITAMIN_B1)?.amount?.toFloat(),
                vitaminB2Milli = getNutrient(Nutrient.VITAMIN_B2)?.amount?.toFloat(),
                vitaminB3Milli = getNutrient(Nutrient.VITAMIN_B3)?.amount?.toFloat(),
                vitaminB5Milli = getNutrient(Nutrient.VITAMIN_B5)?.amount?.toFloat(),
                vitaminB6Milli = getNutrient(Nutrient.VITAMIN_B6)?.amount?.toFloat(),
                vitaminB7Micro = getNutrient(Nutrient.VITAMIN_B7)?.amount?.toFloat(),
                vitaminB9Micro = getNutrient(Nutrient.VITAMIN_B9)?.amount?.toFloat(),
                vitaminB12Micro = getNutrient(Nutrient.VITAMIN_B12)?.amount?.toFloat(),
                vitaminCMilli = getNutrient(Nutrient.VITAMIN_C)?.amount?.toFloat(),
                vitaminDMicro = getNutrient(Nutrient.VITAMIN_D)?.amount?.toFloat(),
                vitaminEMilli = getNutrient(Nutrient.VITAMIN_E)?.amount?.toFloat(),
                vitaminKMicro = getNutrient(Nutrient.VITAMIN_K)?.amount?.toFloat(),
                manganeseMilli = getNutrient(Nutrient.MANGANESE)?.amount?.toFloat(),
                magnesiumMilli = getNutrient(Nutrient.MAGNESIUM)?.amount?.toFloat(),
                potassiumMilli = getNutrient(Nutrient.POTASSIUM)?.amount?.toFloat(),
                calciumMilli = getNutrient(Nutrient.CALCIUM)?.amount?.toFloat(),
                copperMilli = getNutrient(Nutrient.COPPER)?.amount?.toFloat(),
                zincMilli = getNutrient(Nutrient.ZINC)?.amount?.toFloat(),
                sodiumMilli = getNutrient(Nutrient.SODIUM)?.amount?.toFloat(),
                ironMilli = getNutrient(Nutrient.IRON)?.amount?.toFloat(),
                phosphorusMilli = getNutrient(Nutrient.PHOSPHORUS)?.amount?.toFloat(),
                seleniumMicro = getNutrient(Nutrient.SELENIUM)?.amount?.toFloat(),
                iodineMicro = null,
                chromiumMicro = null
            ),
            packageWeight = null,
            servingWeight = null
        )
    }
}
