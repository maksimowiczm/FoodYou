package com.maksimowiczm.foodyou.food.infrastructure.usda

import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.domain.food.NutrientsHelper
import com.maksimowiczm.foodyou.food.domain.entity.RemoteNutritionFacts
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.food.infrastructure.network.UnitType
import com.maksimowiczm.foodyou.food.infrastructure.network.multiplier
import com.maksimowiczm.foodyou.food.infrastructure.usda.model.Food
import com.maksimowiczm.foodyou.food.infrastructure.usda.model.FoodNutrient
import com.maksimowiczm.foodyou.food.infrastructure.usda.model.Nutrient

private val allowedServingUnits by lazy { setOf("g", "GRM", "ml", "MLT") }
private val liquidServingUnits by lazy { setOf("ml", "MLT") }

internal class USDAProductMapper {
    fun toRemoteProduct(food: Food) =
        with(food) {
            val proteins = getNutrient(Nutrient.PROTEIN)?.normalize()
            val carbohydrates = getNutrient(Nutrient.CARBOHYDRATE)?.normalize()
            val fats = getNutrient(Nutrient.FAT)?.normalize()

            val energy =
                getNutrient(Nutrient.CALORIES)?.amount
                    ?: getNutrient(Nutrient.CALORIES_ALTERNATIVE)?.amount
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
                        saturatedFats = getNutrient(Nutrient.SATURATED_FAT)?.normalize(),
                        transFats = getNutrient(Nutrient.TRANS_FAT)?.normalize(),
                        monounsaturatedFats =
                            getNutrient(Nutrient.MONOUNSATURATED_FAT)?.normalize(),
                        polyunsaturatedFats =
                            getNutrient(Nutrient.POLYUNSATURATED_FAT)?.normalize(),
                        omega3 = null,
                        omega6 = null,
                        sugars = getNutrient(Nutrient.SUGARS)?.normalize(),
                        addedSugars = getNutrient(Nutrient.ADDED_SUGARS)?.normalize(),
                        salt = null,
                        dietaryFiber = getNutrient(Nutrient.FIBER)?.normalize(),
                        solubleFiber = null,
                        insolubleFiber = null,
                        cholesterol = getNutrient(Nutrient.CHOLESTEROL)?.normalize(),
                        caffeine = getNutrient(Nutrient.CAFFEINE)?.normalize(),
                        vitaminA = getNutrient(Nutrient.VITAMIN_A)?.normalize(),
                        vitaminB1 = getNutrient(Nutrient.VITAMIN_B1)?.normalize(),
                        vitaminB2 = getNutrient(Nutrient.VITAMIN_B2)?.normalize(),
                        vitaminB3 = getNutrient(Nutrient.VITAMIN_B3)?.normalize(),
                        vitaminB5 = getNutrient(Nutrient.VITAMIN_B5)?.normalize(),
                        vitaminB6 = getNutrient(Nutrient.VITAMIN_B6)?.normalize(),
                        vitaminB7 = getNutrient(Nutrient.VITAMIN_B7)?.normalize(),
                        vitaminB9 = getNutrient(Nutrient.VITAMIN_B9)?.normalize(),
                        vitaminB12 = getNutrient(Nutrient.VITAMIN_B12)?.normalize(),
                        vitaminC = getNutrient(Nutrient.VITAMIN_C)?.normalize(),
                        vitaminD = getNutrient(Nutrient.VITAMIN_D)?.normalize(),
                        vitaminE = getNutrient(Nutrient.VITAMIN_E)?.normalize(),
                        vitaminK = getNutrient(Nutrient.VITAMIN_K)?.normalize(),
                        manganese = getNutrient(Nutrient.MANGANESE)?.normalize(),
                        magnesium = getNutrient(Nutrient.MAGNESIUM)?.normalize(),
                        potassium = getNutrient(Nutrient.POTASSIUM)?.normalize(),
                        calcium = getNutrient(Nutrient.CALCIUM)?.normalize(),
                        copper = getNutrient(Nutrient.COPPER)?.normalize(),
                        zinc = getNutrient(Nutrient.ZINC)?.normalize(),
                        sodium = getNutrient(Nutrient.SODIUM)?.normalize(),
                        iron = getNutrient(Nutrient.IRON)?.normalize(),
                        phosphorus = getNutrient(Nutrient.PHOSPHORUS)?.normalize(),
                        selenium = getNutrient(Nutrient.SELENIUM)?.normalize(),
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
    val from = UnitType.Companion.fromString(unit) ?: return null
    return (amount * multiplier(UnitType.GRAMS, from))
}
