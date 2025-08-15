package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.food.Weighted
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId

/**
 * @param id Unique identifier for the food item.
 * @param headline A short description or name of the food item.
 * @param totalWeight Total weight of the food item in grams, or null if not applicable.
 * @param servingWeight Weight of a single serving of the food item in grams, or null if not
 *   applicable.
 * @param nutritionFacts Nutritional information for the food item per 100 grams or milliliters.
 * @param isLiquid Indicates whether the food item is a liquid (e.g., soup, juice) or solid (e.g.,
 *   bread, meat).
 */
sealed interface Food : Weighted {
    val id: FoodId
    val headline: String
    override val totalWeight: Double?
    override val servingWeight: Double?
    val nutritionFacts: NutritionFacts
    val isLiquid: Boolean
}
