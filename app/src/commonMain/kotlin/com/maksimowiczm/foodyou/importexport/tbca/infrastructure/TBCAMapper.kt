package com.maksimowiczm.foodyou.importexport.tbca.infrastructure

import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.food.domain.entity.RemoteNutritionFacts
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.importexport.tbca.dto.TBCAFoodDto
import com.maksimowiczm.foodyou.importexport.tbca.dto.TBCANutrientDto

/**
 * Maps TBCA (Brazilian Food Composition Table) DTOs to domain entities.
 *
 * TBCA provides nutritional data for Brazilian foods with values per 100g.
 * Handles unit conversions (mg -> g, mcg -> g) and special values ("NA", "tr").
 */
internal class TBCAMapper {

    /**
     * Converts a TBCA food DTO to a RemoteProduct
     */
    fun toRemoteProduct(dto: TBCAFoodDto): RemoteProduct {
        val source = FoodSource(
            type = FoodSource.Type.TBCA,
            url = null // TBCA doesn't have individual URLs for foods
        )

        return RemoteProduct(
            name = dto.description.trim(),
            brand = null, // TBCA doesn't include brand information
            barcode = null, // TBCA doesn't include barcodes
            nutritionFacts = createNutritionFacts(dto.nutrients),
            packageWeight = null,
            servingWeight = 100.0, // TBCA data is always per 100g
            source = source,
            isLiquid = false // Cannot determine from TBCA data
        )
    }

    /**
     * Creates RemoteNutritionFacts from TBCA nutrient list
     */
    private fun createNutritionFacts(nutrients: List<TBCANutrientDto>): RemoteNutritionFacts {
        return RemoteNutritionFacts(
            // Energy - use kcal
            energy = getNutrientValue(nutrients, "Energia", "kcal"),

            // Macronutrients (already in grams)
            proteins = getNutrientValue(nutrients, "Proteína", "g"),
            carbohydrates = getNutrientValue(nutrients, "Carboidrato total", "g"),
            fats = getNutrientValue(nutrients, "Lipídios", "g"),

            // Fatty acids (already in grams)
            saturatedFats = getNutrientValue(nutrients, "Ácidos graxos saturados", "g"),
            transFats = getNutrientValue(nutrients, "Ácidos graxos trans", "g"),
            monounsaturatedFats = getNutrientValue(nutrients, "Ácidos graxos monoinsaturados", "g"),
            polyunsaturatedFats = getNutrientValue(nutrients, "Ácidos graxos poliinsaturados", "g"),

            // Omega fatty acids - TBCA doesn't provide these separately
            omega3 = null,
            omega6 = null,

            // Carbohydrate details
            sugars = null, // TBCA doesn't provide total sugars
            addedSugars = getNutrientValue(nutrients, "Açúcar de adição", "g"),
            dietaryFiber = getNutrientValue(nutrients, "Fibra alimentar", "g"),
            solubleFiber = null, // TBCA doesn't provide separately
            insolubleFiber = null, // TBCA doesn't provide separately

            // Other
            salt = getNutrientValue(nutrients, "Sal de adição", "g"),
            cholesterol = getNutrientValueInMg(nutrients, "Colesterol")?.let { it / 1000.0 }, // mg -> g
            caffeine = null, // TBCA doesn't provide caffeine

            // Vitamins
            vitaminA = getNutrientValueInMcg(nutrients, "Vitamina A (RAE)")?.let { it / 1_000_000.0 }, // mcg -> g
            vitaminB1 = getNutrientValueInMg(nutrients, "Tiamina")?.let { it / 1000.0 }, // mg -> g
            vitaminB2 = getNutrientValueInMg(nutrients, "Riboflavina")?.let { it / 1000.0 }, // mg -> g
            vitaminB3 = getNutrientValueInMg(nutrients, "Niacina")?.let { it / 1000.0 }, // mg -> g
            vitaminB5 = null, // TBCA doesn't provide B5
            vitaminB6 = getNutrientValueInMg(nutrients, "Vitamina B6")?.let { it / 1000.0 }, // mg -> g
            vitaminB7 = null, // TBCA doesn't provide B7
            vitaminB9 = getNutrientValueInMcg(nutrients, "Equivalente de folato")?.let { it / 1_000_000.0 }, // mcg -> g
            vitaminB12 = getNutrientValueInMcg(nutrients, "Vitamina B12")?.let { it / 1_000_000.0 }, // mcg -> g
            vitaminC = getNutrientValueInMg(nutrients, "Vitamina C")?.let { it / 1000.0 }, // mg -> g
            vitaminD = getNutrientValueInMcg(nutrients, "Vitamina D")?.let { it / 1_000_000.0 }, // mcg -> g
            vitaminE = getNutrientValueInMg(nutrients, "Alfa-tocoferol (Vitamina E)")?.let { it / 1000.0 }, // mg -> g
            vitaminK = null, // TBCA doesn't provide vitamin K

            // Minerals (all in mg, need to convert to g)
            calcium = getNutrientValueInMg(nutrients, "Cálcio")?.let { it / 1000.0 },
            iron = getNutrientValueInMg(nutrients, "Ferro")?.let { it / 1000.0 },
            sodium = getNutrientValueInMg(nutrients, "Sódio")?.let { it / 1000.0 },
            magnesium = getNutrientValueInMg(nutrients, "Magnésio")?.let { it / 1000.0 },
            phosphorus = getNutrientValueInMg(nutrients, "Fósforo")?.let { it / 1000.0 },
            potassium = getNutrientValueInMg(nutrients, "Potássio")?.let { it / 1000.0 },
            zinc = getNutrientValueInMg(nutrients, "Zinco")?.let { it / 1000.0 },
            copper = getNutrientValueInMg(nutrients, "Cobre")?.let { it / 1000.0 },
            selenium = getNutrientValueInMcg(nutrients, "Selênio")?.let { it / 1_000_000.0 }, // mcg -> g
            manganese = null, // TBCA doesn't provide manganese consistently
            iodine = null, // TBCA doesn't provide iodine
            chromium = null // TBCA doesn't provide chromium
        )
    }

    /**
     * Gets a nutrient value that is already in grams
     */
    private fun getNutrientValue(
        nutrients: List<TBCANutrientDto>,
        componentName: String,
        expectedUnit: String
    ): Double? {
        val nutrient = nutrients.find {
            it.component == componentName && it.units == expectedUnit
        }

        return parseNutrientValue(nutrient?.valuePer100g)
    }

    /**
     * Gets a nutrient value that is in milligrams
     */
    private fun getNutrientValueInMg(
        nutrients: List<TBCANutrientDto>,
        componentName: String
    ): Double? {
        val nutrient = nutrients.find {
            it.component == componentName && it.units == "mg"
        }
        return parseNutrientValue(nutrient?.valuePer100g)
    }

    /**
     * Gets a nutrient value that is in micrograms
     */
    private fun getNutrientValueInMcg(
        nutrients: List<TBCANutrientDto>,
        componentName: String
    ): Double? {
        val nutrient = nutrients.find {
            it.component == componentName && it.units == "mcg"
        }
        return parseNutrientValue(nutrient?.valuePer100g)
    }

    /**
     * Parses a nutrient value string, handling special cases:
     * - "NA" -> null (not available)
     * - "tr" -> 0.0 (trace amount)
     * - empty/blank -> null
     * - numeric -> parsed value
     */
    private fun parseNutrientValue(value: String?): Double? {
        if (value.isNullOrBlank()) return null

        return when (value.trim().lowercase()) {
            "na" -> null // Not available
            "tr" -> 0.0  // Trace amount (very small, treat as zero)
            else -> value.replace(',', '.').toDoubleOrNull()
        }
    }
}
