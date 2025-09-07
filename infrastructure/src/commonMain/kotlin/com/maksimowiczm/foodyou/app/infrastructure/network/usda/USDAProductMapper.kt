package com.maksimowiczm.foodyou.app.infrastructure.network.usda

import com.maksimowiczm.foodyou.app.infrastructure.network.UnitType
import com.maksimowiczm.foodyou.app.infrastructure.network.multiplier
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.model.Food
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.model.FoodNutrient
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.model.Nutrient.*
import com.maksimowiczm.foodyou.food.domain.entity.RemoteNutritionFacts
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.domain.food.NutrientsHelper

private val allowedServingUnits by lazy { setOf("g", "GRM", "ml", "MLT") }
private val liquidServingUnits by lazy { setOf("ml", "MLT") }

internal class USDAProductMapper {
    fun toRemoteProduct(food: Food) =
        with(food) {
            val proteins = getNutrient(PROTEIN)?.normalize()
            val carbohydrates = getNutrient(CARBOHYDRATE)?.normalize()
            val fats = getNutrient(FAT)?.normalize()

            val energy =
                getNutrient(CALORIES)?.amount
                    ?: getNutrient(CALORIES_ALTERNATIVE)?.amount
                    ?: if (proteins == null || carbohydrates == null || fats == null) {
                        null
                    } else {
                        NutrientsHelper.calculateEnergy(
                            proteins = proteins,
                            carbohydrates = carbohydrates,
                            fats = fats,
                        )
                    }

            val source = FoodSource(type = FoodSource.Type.USDA, url = url)

            val servingWeight =
                if (allowedServingUnits.contains(servingSizeUnit)) {
                    servingSize
                } else {
                    null
                }

            val isLiquid = liquidServingUnits.contains(servingSizeUnit)

            RemoteProduct(
                name = description.trim(),
                brand = brand?.trim(),
                barcode = barcode?.trim(),
                nutritionFacts =
                    RemoteNutritionFacts(
                        proteins = proteins,
                        carbohydrates = carbohydrates,
                        fats = fats,
                        energy = energy,
                        saturatedFats = getNutrient(SATURATED_FAT)?.normalize(),
                        transFats = getNutrient(TRANS_FAT)?.normalize(),
                        monounsaturatedFats = getNutrient(MONOUNSATURATED_FAT)?.normalize(),
                        polyunsaturatedFats = getNutrient(POLYUNSATURATED_FAT)?.normalize(),
                        omega3 = null,
                        omega6 = null,
                        sugars = getNutrient(SUGARS)?.normalize(),
                        addedSugars = getNutrient(ADDED_SUGARS)?.normalize(),
                        salt = null,
                        dietaryFiber = getNutrient(FIBER)?.normalize(),
                        solubleFiber = null,
                        insolubleFiber = null,
                        cholesterol = getNutrient(CHOLESTEROL)?.normalize(),
                        caffeine = getNutrient(CAFFEINE)?.normalize(),
                        vitaminA = getNutrient(VITAMIN_A)?.normalize(),
                        vitaminB1 = getNutrient(VITAMIN_B1)?.normalize(),
                        vitaminB2 = getNutrient(VITAMIN_B2)?.normalize(),
                        vitaminB3 = getNutrient(VITAMIN_B3)?.normalize(),
                        vitaminB5 = getNutrient(VITAMIN_B5)?.normalize(),
                        vitaminB6 = getNutrient(VITAMIN_B6)?.normalize(),
                        vitaminB7 = getNutrient(VITAMIN_B7)?.normalize(),
                        vitaminB9 = getNutrient(VITAMIN_B9)?.normalize(),
                        vitaminB12 = getNutrient(VITAMIN_B12)?.normalize(),
                        vitaminC = getNutrient(VITAMIN_C)?.normalize(),
                        vitaminD = getNutrient(VITAMIN_D)?.normalize(),
                        vitaminE = getNutrient(VITAMIN_E)?.normalize(),
                        vitaminK = getNutrient(VITAMIN_K)?.normalize(),
                        manganese = getNutrient(MANGANESE)?.normalize(),
                        magnesium = getNutrient(MAGNESIUM)?.normalize(),
                        potassium = getNutrient(POTASSIUM)?.normalize(),
                        calcium = getNutrient(CALCIUM)?.normalize(),
                        copper = getNutrient(COPPER)?.normalize(),
                        zinc = getNutrient(ZINC)?.normalize(),
                        sodium = getNutrient(SODIUM)?.normalize(),
                        iron = getNutrient(IRON)?.normalize(),
                        phosphorus = getNutrient(PHOSPHORUS)?.normalize(),
                        selenium = getNutrient(SELENIUM)?.normalize(),
                        iodine = null,
                        chromium = null,
                    ),
                packageWeight = null,
                servingWeight = servingWeight,
                source = source,
                isLiquid = isLiquid,
            )
        }
}

private fun FoodNutrient.normalize(): Double? {
    val amount = amount
    val from = UnitType.fromString(unit) ?: return null
    return (amount * multiplier(UnitType.GRAMS, from))
}
