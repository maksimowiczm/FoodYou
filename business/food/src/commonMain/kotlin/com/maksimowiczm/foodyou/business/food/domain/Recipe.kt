package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.sum
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId

data class Recipe(
    override val id: FoodId.Recipe,
    val name: String,
    val servings: Int,
    val ingredients: List<RecipeIngredient>,
    val note: String?,
    val isLiquid: Boolean,
) : Food {
    override val totalWeight = ingredients.mapNotNull { it.weight }.sum()

    override val servingWeight = totalWeight / servings

    override val nutritionFacts: NutritionFacts by lazy {
        if (ingredients.isEmpty() || totalWeight == 0.0) {
            NutritionFacts.Empty
        } else {
            ingredients.mapNotNull { it.nutritionFacts }.sum() / totalWeight
        }
    }
}
