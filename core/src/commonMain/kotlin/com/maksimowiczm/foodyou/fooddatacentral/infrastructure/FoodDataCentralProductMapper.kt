package com.maksimowiczm.foodyou.fooddatacentral.infrastructure

import com.maksimowiczm.foodyou.common.domain.Volume
import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.infrastructure.room.NutrientsEntity
import com.maksimowiczm.foodyou.common.infrastructure.room.NutrientsMapper
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductId
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters
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
                dataType =
                    FoodDataCentralSearchParameters.DataType.entries.first {
                        it.filter == dataType
                    },
                nutrients = nutrients,
            )
        }

    fun foodDataCentralProduct(entity: FoodDataCentralProductEntity): FoodDataCentralProduct =
        with(entity) {
            val servingQuantity = parseServing(servingSize, servingSizeUnit)
            val packageQuantity = parsePackage(packageWeight)

            FoodDataCentralProduct(
                id = FoodDataCentralProductId(fdcId),
                name = description,
                brand = brandOwner,
                barcode = gtinUpc,
                source = "https://fdc.nal.usda.gov/food-details/$fdcId/nutrients",
                nutritionFacts = nutrientsMapper.toNutritionFats(entity.nutrients),
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
            )
        }
}

private fun parseServing(weight: Double?, unit: String?): AbsoluteQuantity? {
    val weight = weight ?: return null
    val unitStr = unit ?: return null

    WeightUnit.fromSymbolOrNull(unitStr)?.let {
        return AbsoluteQuantity.Weight(Weight.from(weight, it))
    }

    VolumeUnit.fromSymbolOrNull(unitStr)?.let {
        return AbsoluteQuantity.Volume(Volume.from(weight, it))
    }

    return null
}

private fun parsePackage(weight: String?): AbsoluteQuantity? {
    return findQuantity(weight)
}

private val quantityRegex = Regex("""(\d+(?:\.\d+)?\s*[a-zA-Z. ]+)""")

private fun findQuantity(value: String?): AbsoluteQuantity? {
    if (value == null) return null

    // Try parsing the whole string first
    AbsoluteQuantity.parseOrNull(value)?.let {
        return it
    }

    // Try splitting by slash and parsing parts (common in USDA, e.g. "4.5 oz/128 g")
    if (value.contains("/")) {
        val parts = value.split("/")
        val parsedParts = parts.mapNotNull { AbsoluteQuantity.parseOrNull(it.trim()) }
        if (parsedParts.isNotEmpty()) {
            // Prioritize Weight (especially Grams)
            return parsedParts.filterIsInstance<AbsoluteQuantity.Weight>().maxByOrNull {
                it.weight.unit == WeightUnit.Grams
            } ?: parsedParts.first()
        }
    }

    // Fallback to finding any sequence that looks like a quantity
    val matches = quantityRegex.findAll(value)
    val parsedMatches =
        matches.mapNotNull { AbsoluteQuantity.parseOrNull(it.groupValues[1].trim()) }.toList()

    if (parsedMatches.isNotEmpty()) {
        return parsedMatches.filterIsInstance<AbsoluteQuantity.Weight>().maxByOrNull {
            it.weight.unit == WeightUnit.Grams
        } ?: parsedMatches.first()
    }

    return null
}

private fun FoodNutrient.normalize(): Double? {
    val amount = amount ?: return null
    val from = UnitType.fromString(unit) ?: return null
    return (amount * multiplier(UnitType.GRAMS, from)).takeIf { it >= 0 }
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
