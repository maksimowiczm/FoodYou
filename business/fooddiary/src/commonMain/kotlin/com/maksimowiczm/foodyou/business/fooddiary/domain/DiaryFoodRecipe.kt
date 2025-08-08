package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.sum

data class DiaryFoodRecipe(
    override val name: String,
    val servings: Int,
    val ingredients: List<DiaryFoodRecipeIngredient>,
    override val isLiquid: Boolean,
) : DiaryFood {
    override val nutritionFacts: NutritionFacts by lazy {
        ingredients.map { it.nutritionFacts }.sum()
    }

    override val servingWeight: Double
        get() = totalWeight / servings

    override val totalWeight: Double
        get() = ingredients.sumOf { it.weight }
}
