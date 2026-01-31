package com.maksimowiczm.foodyou.fooddatacentral.infrastructure

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Barcode
import com.maksimowiczm.foodyou.common.domain.food.FluidOunces
import com.maksimowiczm.foodyou.common.domain.food.FoodBrand
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.Milliliters
import com.maksimowiczm.foodyou.common.domain.food.Ounces
import com.maksimowiczm.foodyou.common.infrastructure.food.NutrientsEntity
import com.maksimowiczm.foodyou.common.infrastructure.food.NutrientsMapper
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.Food
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.FoodNutrient
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.model.Nutrient
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralProductEntity

internal class FoodDataCentralProductMapper {
    private val nutrientsMapper = NutrientsMapper()

    fun foodDataCentralProductEntity(network: Food): FoodDataCentralProductEntity =
        with(network) {
            val energy =
                (getNutrient(Nutrient.ENERGY) ?: getNutrient(Nutrient.ENERGY_ALTERNATIVE))?.amount

            val nutrients =
                NutrientsEntity(
                    energy = energy,
                    proteins = getNutrient(Nutrient.PROTEIN)?.normalize(),
                    fats = getNutrient(Nutrient.FAT)?.normalize(),
                    saturatedFats = getNutrient(Nutrient.SATURATED_FAT)?.normalize(),
                    transFats = getNutrient(Nutrient.TRANS_FAT)?.normalize(),
                    monounsaturatedFats = getNutrient(Nutrient.MONOUNSATURATED_FAT)?.normalize(),
                    polyunsaturatedFats = getNutrient(Nutrient.POLYUNSATURATED_FAT)?.normalize(),
                    omega3 = null,
                    omega6 = null,
                    carbohydrates = getNutrient(Nutrient.CARBOHYDRATE)?.normalize(),
                    sugars = getNutrient(Nutrient.SUGARS)?.normalize(),
                    addedSugars = getNutrient(Nutrient.ADDED_SUGARS)?.normalize(),
                    dietaryFiber = getNutrient(Nutrient.FIBER)?.normalize(),
                    solubleFiber = null,
                    insolubleFiber = null,
                    salt = null,
                    cholesterol = getNutrient(Nutrient.CHOLESTEROL)?.normalize(),
                    caffeine = getNutrient(Nutrient.CAFFEINE)?.normalize(),
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
                )

            return FoodDataCentralProductEntity(
                fdcId = fdcId,
                description = description,
                brandOwner = brandOwner.takeIfNotBlank(),
                brandName = brandName.takeIfNotBlank(),
                gtinUpc = gtinUpc.takeIfNotBlank(),
                servingSize = servingSize,
                servingSizeUnit = servingSizeUnit,
                packageWeight = packageWeight.takeIfNotBlank(),
                nutrients = nutrients,
            )
        }

    fun foodDataCentralProduct(entity: FoodDataCentralProductEntity): FoodDataCentralProduct =
        with(entity) {
            val servingQuantity = parseServing(servingSize, servingSizeUnit)
            val packageQuantity = parsePackage(packageWeight)
            val isLiquid =
                when {
                    servingQuantity is AbsoluteQuantity.Volume -> true
                    packageQuantity is AbsoluteQuantity.Volume -> true
                    else -> false
                }

            FoodDataCentralProduct(
                identity = FoodDataCentralProductIdentity(fdcId),
                name = FoodName(english = description, fallback = description),
                brand = brandOwner?.let { FoodBrand(it) },
                barcode = gtinUpc?.let { Barcode(it) },
                note = null,
                image = null,
                source =
                    FoodSource.FoodDataCentral(
                        "https://fdc.nal.usda.gov/food-details/$fdcId/nutrients"
                    ),
                nutritionFacts = nutrientsMapper.toNutritionFats(entity.nutrients),
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                isLiquid = isLiquid,
            )
        }
}

private fun parseServing(weight: Double?, unit: String?): AbsoluteQuantity? {
    val weight = weight ?: return null
    val unit = unit?.lowercase() ?: return null

    return when (unit) {
        "ml",
        "mlt" -> AbsoluteQuantity.Volume(Milliliters(weight))

        "g",
        "grm" -> AbsoluteQuantity.Weight(Grams(weight))

        "oz",
        "oz." -> AbsoluteQuantity.Weight(Ounces(weight))

        "fl oz",
        "fl.oz",
        "fl. oz",
        "fl.oz." -> AbsoluteQuantity.Volume(FluidOunces(weight))

        else -> null
    }
}

private fun parsePackage(weight: String?): AbsoluteQuantity? {
    // TODO
    return null
}

private fun FoodNutrient.normalize(): Double? {
    val amount = amount ?: return null
    val from = UnitType.fromString(unit) ?: return null
    return (amount * multiplier(UnitType.GRAMS, from))
}

/**
 * Helper function to convert a unit to a multiplier for a target unit.
 *
 * @param targetUnit The unit to convert to. It can be GRAMS, MILLIGRAMS, or MICROGRAMS.
 * @param from The original unit to convert from. Defaults to GRAMS.
 * @return The multiplier to convert the value from the original unit to the target unit.
 */
private fun multiplier(targetUnit: UnitType, from: UnitType = UnitType.GRAMS) =
    when (targetUnit) {
        UnitType.GRAMS ->
            when (from) {
                UnitType.GRAMS -> 1.0
                UnitType.MILLIGRAMS -> 0.001
                UnitType.MICROGRAMS -> 0.000001
            }

        UnitType.MILLIGRAMS ->
            when (from) {
                UnitType.GRAMS -> 1000.0
                UnitType.MILLIGRAMS -> 1.0
                UnitType.MICROGRAMS -> 0.001
            }

        UnitType.MICROGRAMS ->
            when (from) {
                UnitType.GRAMS -> 1000000.0
                UnitType.MILLIGRAMS -> 1000.0
                UnitType.MICROGRAMS -> 1.0
            }
    }

private enum class UnitType {
    GRAMS,
    MILLIGRAMS,
    MICROGRAMS;

    companion object {
        fun fromString(unit: String): UnitType? =
            when (unit.lowercase()) {
                "g" -> GRAMS
                "mg" -> MILLIGRAMS
                "ug",
                "µg",
                "mcg" -> MICROGRAMS

                else -> null
            }
    }
}

private fun String?.takeIfNotBlank(): String? = this?.takeIf { it.isNotBlank() }
