package com.maksimowiczm.foodyou.feature.food.data.network.usda

import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.food.data.network.UnitType
import com.maksimowiczm.foodyou.feature.food.data.network.UnitType.GRAMS
import com.maksimowiczm.foodyou.feature.food.data.network.UnitType.MICROGRAMS
import com.maksimowiczm.foodyou.feature.food.data.network.UnitType.MILLIGRAMS
import com.maksimowiczm.foodyou.feature.food.data.network.multiplier
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.RemoteNutritionFacts
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.usda.model.Food
import com.maksimowiczm.foodyou.feature.usda.model.FoodNutrient
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.ADDED_SUGARS
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.CAFFEINE
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.CALCIUM
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.CALORIES
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.CALORIES_ALTERNATIVE
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.CARBOHYDRATE
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.CHOLESTEROL
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.COPPER
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.FAT
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.FIBER
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.IRON
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.MAGNESIUM
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.MANGANESE
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.MONOUNSATURATED_FAT
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.PHOSPHORUS
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.POLYUNSATURATED_FAT
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.POTASSIUM
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.PROTEIN
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.SATURATED_FAT
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.SELENIUM
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.SODIUM
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.SUGARS
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.TRANS_FAT
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_A
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_B1
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_B12
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_B2
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_B3
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_B5
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_B6
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_B9
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_C
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_D
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_E
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.VITAMIN_K
import com.maksimowiczm.foodyou.feature.usda.model.Nutrient.ZINC

internal class USDAProductMapper {
    fun toRemoteProduct(food: Food) = with(food) {
        val proteins = getNutrient(PROTEIN)?.normalize(GRAMS)
        val carbohydrates = getNutrient(CARBOHYDRATE)?.normalize(GRAMS)
        val fats = getNutrient(FAT)?.normalize(GRAMS)

        val calories: Float? = getNutrient(CALORIES)?.amount?.toFloat()
            ?: getNutrient(CALORIES_ALTERNATIVE)?.amount?.toFloat()
            ?: if (proteins == null || carbohydrates == null || fats == null) {
                null
            } else {
                NutrientsHelper.calculateEnergy(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats
                )
            }

        val source = FoodSource(
            type = FoodSource.Type.USDA,
            url = url
        )

        val servingWeight = if (servingSizeUnit == "g" || servingSizeUnit == "GRM") {
            servingSize?.toFloat()
        } else {
            null
        }

        RemoteProduct(
            name = description.trim(),
            brand = brand?.trim(),
            barcode = barcode?.trim(),
            nutritionFacts = RemoteNutritionFacts(
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                energy = calories,
                saturatedFats = getNutrient(SATURATED_FAT)?.normalize(GRAMS),
                transFats = getNutrient(TRANS_FAT)?.normalize(GRAMS),
                monounsaturatedFats = getNutrient(MONOUNSATURATED_FAT)?.normalize(GRAMS),
                polyunsaturatedFats = getNutrient(POLYUNSATURATED_FAT)?.normalize(GRAMS),
                omega3 = null,
                omega6 = null,
                sugars = getNutrient(SUGARS)?.normalize(GRAMS),
                addedSugars = getNutrient(ADDED_SUGARS)?.normalize(GRAMS),
                salt = null,
                fiber = getNutrient(FIBER)?.normalize(GRAMS),
                solubleFiber = null,
                insolubleFiber = null,
                cholesterolMilli = getNutrient(CHOLESTEROL)?.normalize(MILLIGRAMS),
                caffeineMilli = getNutrient(CAFFEINE)?.normalize(MILLIGRAMS),
                vitaminAMicro = getNutrient(VITAMIN_A)?.normalize(MICROGRAMS),
                vitaminB1Milli = getNutrient(VITAMIN_B1)?.normalize(MILLIGRAMS),
                vitaminB2Milli = getNutrient(VITAMIN_B2)?.normalize(MILLIGRAMS),
                vitaminB3Milli = getNutrient(VITAMIN_B3)?.normalize(MILLIGRAMS),
                vitaminB5Milli = getNutrient(VITAMIN_B5)?.normalize(MILLIGRAMS),
                vitaminB6Milli = getNutrient(VITAMIN_B6)?.normalize(MILLIGRAMS),
                vitaminB7Micro = getNutrient(Nutrient.VITAMIN_B7)?.normalize(MICROGRAMS),
                vitaminB9Micro = getNutrient(VITAMIN_B9)?.normalize(MICROGRAMS),
                vitaminB12Micro = getNutrient(VITAMIN_B12)?.normalize(MICROGRAMS),
                vitaminCMilli = getNutrient(VITAMIN_C)?.normalize(MILLIGRAMS),
                vitaminDMicro = getNutrient(VITAMIN_D)?.normalize(MICROGRAMS),
                vitaminEMilli = getNutrient(VITAMIN_E)?.normalize(MILLIGRAMS),
                vitaminKMicro = getNutrient(VITAMIN_K)?.normalize(MICROGRAMS),
                manganeseMilli = getNutrient(MANGANESE)?.normalize(MILLIGRAMS),
                magnesiumMilli = getNutrient(MAGNESIUM)?.normalize(MILLIGRAMS),
                potassiumMilli = getNutrient(POTASSIUM)?.normalize(MILLIGRAMS),
                calciumMilli = getNutrient(CALCIUM)?.normalize(MILLIGRAMS),
                copperMilli = getNutrient(COPPER)?.normalize(MILLIGRAMS),
                zincMilli = getNutrient(ZINC)?.normalize(MILLIGRAMS),
                sodiumMilli = getNutrient(SODIUM)?.normalize(MILLIGRAMS),
                ironMilli = getNutrient(IRON)?.normalize(MILLIGRAMS),
                phosphorusMilli = getNutrient(PHOSPHORUS)?.normalize(MILLIGRAMS),
                seleniumMicro = getNutrient(SELENIUM)?.normalize(MICROGRAMS),
                iodineMicro = null,
                chromiumMicro = null
            ),
            packageWeight = null,
            servingWeight = servingWeight,
            source = source
        )
    }
}

private fun FoodNutrient.normalize(target: UnitType): Float? {
    val amount = amount
    val from = UnitType.fromString(unit) ?: return null
    return (amount * multiplier(target, from)).toFloat()
}
