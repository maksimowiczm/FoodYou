package com.maksimowiczm.foodyou.food.infrastructure.usda

import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.domain.food.NutrientsHelper
import com.maksimowiczm.foodyou.food.domain.entity.RemoteNutritionFacts
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.food.infrastructure.network.UnitType
import com.maksimowiczm.foodyou.food.infrastructure.network.multiplier
import com.maksimowiczm.foodyou.food.infrastructure.usda.model.AbridgedFoodItem
import com.maksimowiczm.foodyou.food.infrastructure.usda.model.AbridgedFoodNutrient
import com.maksimowiczm.foodyou.food.infrastructure.usda.model.SearchResultFood
import com.maksimowiczm.foodyou.food.infrastructure.usda.model.SearchResultFoodNutrient
import kotlin.jvm.JvmName

internal class UsdaFdcMapper {
    fun toRemoteProduct(food: AbridgedFoodItem): RemoteProduct {
        return with(food) {
            val proteins = getNutrient(foodNutrients, UsdaNutrientId.PROTEIN)?.normalize()
            val carbohydrates = getNutrient(foodNutrients, UsdaNutrientId.CARBOHYDRATE)?.normalize()
            val fats = getNutrient(foodNutrients, UsdaNutrientId.TOTAL_FAT)?.normalize()

            // Try to get energy, or calculate it if we have macros
            val energy =
                getNutrient(foodNutrients, UsdaNutrientId.ENERGY_KCAL)?.amount
                    ?: getNutrient(foodNutrients, UsdaNutrientId.ENERGY_ALTERNATIVE)?.amount
                    ?: if (proteins != null && carbohydrates != null && fats != null) {
                        NutrientsHelper.calculateEnergy(
                            proteins = proteins,
                            carbohydrates = carbohydrates,
                            fats = fats,
                        )
                    } else {
                        null
                    }

            val source =
                FoodSource(
                    type = FoodSource.Type.USDA,
                    url = "https://fdc.nal.usda.gov/food-details/$fdcId/nutrients",
                )

            RemoteProduct(
                name = description.trim(),
                brand = brandOwner?.trim(),
                barcode = gtinUpc?.trim(),
                nutritionFacts =
                    createNutritionFacts(foodNutrients, energy, proteins, carbohydrates, fats),
                packageWeight = null,
                servingWeight = null, // AbridgedFoodItem doesn't include serving size
                source = source,
                isLiquid = false, // Cannot determine from abridged data
            )
        }
    }

    fun toRemoteProduct(food: SearchResultFood): RemoteProduct {
        return with(food) {
            val proteins = getNutrient(foodNutrients, UsdaNutrientId.PROTEIN)?.normalize()
            val carbohydrates = getNutrient(foodNutrients, UsdaNutrientId.CARBOHYDRATE)?.normalize()
            val fats = getNutrient(foodNutrients, UsdaNutrientId.TOTAL_FAT)?.normalize()

            // Try to get energy, or calculate it if we have macros
            val energy =
                getNutrient(foodNutrients, UsdaNutrientId.ENERGY_KCAL)?.amount
                    ?: getNutrient(foodNutrients, UsdaNutrientId.ENERGY_ALTERNATIVE)?.amount
                    ?: if (proteins != null && carbohydrates != null && fats != null) {
                        NutrientsHelper.calculateEnergy(
                            proteins = proteins,
                            carbohydrates = carbohydrates,
                            fats = fats,
                        )
                    } else {
                        null
                    }

            val source =
                FoodSource(
                    type = FoodSource.Type.USDA,
                    url = "https://fdc.nal.usda.gov/food-details/$fdcId/nutrients",
                )

            RemoteProduct(
                name = description.trim(),
                brand = brandOwner?.trim(),
                barcode = gtinUpc?.trim(),
                nutritionFacts =
                    createNutritionFacts(foodNutrients, energy, proteins, carbohydrates, fats),
                packageWeight = null,
                servingWeight = null, // SearchResultFood doesn't include serving size
                source = source,
                isLiquid = false, // Cannot determine from search result data
            )
        }
    }

    private fun getNutrient(
        nutrients: List<AbridgedFoodNutrient>,
        number: Int,
    ): AbridgedFoodNutrient? {
        return nutrients.firstOrNull { it.number == number }
    }

    private fun getNutrient(
        nutrients: List<SearchResultFoodNutrient>,
        number: Int,
    ): SearchResultFoodNutrient? {
        return nutrients.firstOrNull { it.number == number }
    }

    @JvmName("createNutritionFactsAbridged")
    private fun createNutritionFacts(
        nutrients: List<AbridgedFoodNutrient>,
        energy: Double?,
        proteins: Double?,
        carbohydrates: Double?,
        fats: Double?,
    ): RemoteNutritionFacts =
        RemoteNutritionFacts(
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            energy = energy,
            saturatedFats = getNutrient(nutrients, UsdaNutrientId.SATURATED_FAT)?.normalize(),
            transFats = getNutrient(nutrients, UsdaNutrientId.TRANS_FAT)?.normalize(),
            monounsaturatedFats =
                getNutrient(nutrients, UsdaNutrientId.MONOUNSATURATED_FAT)?.normalize(),
            polyunsaturatedFats =
                getNutrient(nutrients, UsdaNutrientId.POLYUNSATURATED_FAT)?.normalize(),
            omega3 = null,
            omega6 = null,
            sugars = getNutrient(nutrients, UsdaNutrientId.SUGARS)?.normalize(),
            addedSugars = getNutrient(nutrients, UsdaNutrientId.ADDED_SUGARS)?.normalize(),
            salt = null,
            dietaryFiber = getNutrient(nutrients, UsdaNutrientId.DIETARY_FIBER)?.normalize(),
            solubleFiber = null,
            insolubleFiber = null,
            cholesterol = getNutrient(nutrients, UsdaNutrientId.CHOLESTEROL)?.normalize(),
            caffeine = getNutrient(nutrients, UsdaNutrientId.CAFFEINE)?.normalize(),
            vitaminA = getNutrient(nutrients, UsdaNutrientId.VITAMIN_A)?.normalize(),
            vitaminB1 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B1_THIAMIN)?.normalize(),
            vitaminB2 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B2_RIBOFLAVIN)?.normalize(),
            vitaminB3 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B3_NIACIN)?.normalize(),
            vitaminB5 =
                getNutrient(nutrients, UsdaNutrientId.VITAMIN_B5_PANTOTHENIC_ACID)?.normalize(),
            vitaminB6 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B6)?.normalize(),
            vitaminB7 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B7_BIOTIN)?.normalize(),
            vitaminB9 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B9_FOLATE)?.normalize(),
            vitaminB12 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B12)?.normalize(),
            vitaminC = getNutrient(nutrients, UsdaNutrientId.VITAMIN_C)?.normalize(),
            vitaminD = getNutrient(nutrients, UsdaNutrientId.VITAMIN_D)?.normalize(),
            vitaminE = getNutrient(nutrients, UsdaNutrientId.VITAMIN_E)?.normalize(),
            vitaminK = getNutrient(nutrients, UsdaNutrientId.VITAMIN_K)?.normalize(),
            manganese = getNutrient(nutrients, UsdaNutrientId.MANGANESE)?.normalize(),
            magnesium = getNutrient(nutrients, UsdaNutrientId.MAGNESIUM)?.normalize(),
            potassium = getNutrient(nutrients, UsdaNutrientId.POTASSIUM)?.normalize(),
            calcium = getNutrient(nutrients, UsdaNutrientId.CALCIUM)?.normalize(),
            copper = getNutrient(nutrients, UsdaNutrientId.COPPER)?.normalize(),
            zinc = getNutrient(nutrients, UsdaNutrientId.ZINC)?.normalize(),
            sodium = getNutrient(nutrients, UsdaNutrientId.SODIUM)?.normalize(),
            iron = getNutrient(nutrients, UsdaNutrientId.IRON)?.normalize(),
            phosphorus = getNutrient(nutrients, UsdaNutrientId.PHOSPHORUS)?.normalize(),
            selenium = getNutrient(nutrients, UsdaNutrientId.SELENIUM)?.normalize(),
            iodine = null,
            chromium = null,
        )

    @JvmName("createNutritionFactsSearch")
    private fun createNutritionFacts(
        nutrients: List<SearchResultFoodNutrient>,
        energy: Double?,
        proteins: Double?,
        carbohydrates: Double?,
        fats: Double?,
    ): RemoteNutritionFacts =
        RemoteNutritionFacts(
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            energy = energy,
            saturatedFats = getNutrient(nutrients, UsdaNutrientId.SATURATED_FAT)?.normalize(),
            transFats = getNutrient(nutrients, UsdaNutrientId.TRANS_FAT)?.normalize(),
            monounsaturatedFats =
                getNutrient(nutrients, UsdaNutrientId.MONOUNSATURATED_FAT)?.normalize(),
            polyunsaturatedFats =
                getNutrient(nutrients, UsdaNutrientId.POLYUNSATURATED_FAT)?.normalize(),
            omega3 = null,
            omega6 = null,
            sugars = getNutrient(nutrients, UsdaNutrientId.SUGARS)?.normalize(),
            addedSugars = getNutrient(nutrients, UsdaNutrientId.ADDED_SUGARS)?.normalize(),
            salt = null,
            dietaryFiber = getNutrient(nutrients, UsdaNutrientId.DIETARY_FIBER)?.normalize(),
            solubleFiber = null,
            insolubleFiber = null,
            cholesterol = getNutrient(nutrients, UsdaNutrientId.CHOLESTEROL)?.normalize(),
            caffeine = getNutrient(nutrients, UsdaNutrientId.CAFFEINE)?.normalize(),
            vitaminA = getNutrient(nutrients, UsdaNutrientId.VITAMIN_A)?.normalize(),
            vitaminB1 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B1_THIAMIN)?.normalize(),
            vitaminB2 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B2_RIBOFLAVIN)?.normalize(),
            vitaminB3 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B3_NIACIN)?.normalize(),
            vitaminB5 =
                getNutrient(nutrients, UsdaNutrientId.VITAMIN_B5_PANTOTHENIC_ACID)?.normalize(),
            vitaminB6 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B6)?.normalize(),
            vitaminB7 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B7_BIOTIN)?.normalize(),
            vitaminB9 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B9_FOLATE)?.normalize(),
            vitaminB12 = getNutrient(nutrients, UsdaNutrientId.VITAMIN_B12)?.normalize(),
            vitaminC = getNutrient(nutrients, UsdaNutrientId.VITAMIN_C)?.normalize(),
            vitaminD = getNutrient(nutrients, UsdaNutrientId.VITAMIN_D)?.normalize(),
            vitaminE = getNutrient(nutrients, UsdaNutrientId.VITAMIN_E)?.normalize(),
            vitaminK = getNutrient(nutrients, UsdaNutrientId.VITAMIN_K)?.normalize(),
            manganese = getNutrient(nutrients, UsdaNutrientId.MANGANESE)?.normalize(),
            magnesium = getNutrient(nutrients, UsdaNutrientId.MAGNESIUM)?.normalize(),
            potassium = getNutrient(nutrients, UsdaNutrientId.POTASSIUM)?.normalize(),
            calcium = getNutrient(nutrients, UsdaNutrientId.CALCIUM)?.normalize(),
            copper = getNutrient(nutrients, UsdaNutrientId.COPPER)?.normalize(),
            zinc = getNutrient(nutrients, UsdaNutrientId.ZINC)?.normalize(),
            sodium = getNutrient(nutrients, UsdaNutrientId.SODIUM)?.normalize(),
            iron = getNutrient(nutrients, UsdaNutrientId.IRON)?.normalize(),
            phosphorus = getNutrient(nutrients, UsdaNutrientId.PHOSPHORUS)?.normalize(),
            selenium = getNutrient(nutrients, UsdaNutrientId.SELENIUM)?.normalize(),
            iodine = null,
            chromium = null,
        )

    private fun AbridgedFoodNutrient.normalize(): Double? = normalize(unitName, amount)

    private fun SearchResultFoodNutrient.normalize(): Double? = normalize(unitName, amount)

    /** Normalize nutrient value to grams. Converts from mg or µg to grams if needed. */
    private fun normalize(unitName: String?, amount: Double?): Double? {
        val unitName = unitName ?: return null
        val amount = amount ?: return null
        val from = UnitType.fromString(unitName) ?: return null
        return amount * multiplier(UnitType.GRAMS, from)
    }
}
