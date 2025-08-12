package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

/**
 * Represents a food item in the food diary.
 *
 * @param name The name of the food item.
 * @param nutritionFacts The nutrition facts of the food item per 100g or 100ml.
 * @param servingWeight The weight of a single serving of the food item, if applicable.
 * @param totalWeight The total weight of the food item, if applicable.
 * @param isLiquid Indicates whether the food item is a liquid.
 */
sealed interface DiaryFood {
    val name: String
    val nutritionFacts: NutritionFacts
    val servingWeight: Double?
    val totalWeight: Double?
    val isLiquid: Boolean
    val note: String?
}
