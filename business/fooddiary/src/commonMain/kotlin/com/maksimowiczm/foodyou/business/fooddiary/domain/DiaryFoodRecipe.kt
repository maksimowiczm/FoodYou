package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.sum

/**
 * Represents a food recipe in the food diary.
 *
 * @param name The name of the recipe.
 * @param servings The number of servings the recipe yields.
 * @param ingredients The list of ingredients used in the recipe.
 * @param isLiquid Indicates whether the recipe is a liquid.
 */
data class DiaryFoodRecipe(
    override val name: String,
    val servings: Int,
    val ingredients: List<DiaryFoodRecipeIngredient>,
    override val isLiquid: Boolean,
) : DiaryFood {

    /** The nutrition facts of the food item per 100g or 100ml. */
    override val nutritionFacts: NutritionFacts by lazy {
        ingredients.map { it.nutritionFacts }.sum() / totalWeight * 100.0
    }

    override val servingWeight: Double
        get() = totalWeight / servings

    override val totalWeight: Double
        get() = ingredients.sumOf { it.weight }
}
