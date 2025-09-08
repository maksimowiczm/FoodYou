package com.maksimowiczm.foodyou.food.domain.entity

import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.food.WeightedStrict
import com.maksimowiczm.foodyou.shared.domain.food.sum

/**
 * Represents a recipe in the food domain.
 *
 * @param id The unique identifier for the recipe.
 * @param name The name of the recipe.
 * @param servings The number of servings the recipe yields.
 * @param ingredients The list of ingredients used in the recipe.
 * @param note An optional note for the recipe.
 * @param isLiquid Indicates whether the recipe is a liquid.
 */
data class Recipe(
    override val id: FoodId.Recipe,
    val name: String,
    val servings: Int,
    val ingredients: List<RecipeIngredient>,
    val note: String?,
    override val isLiquid: Boolean,
) : Food, WeightedStrict {
    override val headline: String = name

    override val totalWeight = ingredients.mapNotNull { it.weight }.sum()

    override val servingWeight = totalWeight / servings

    override val nutritionFacts: NutritionFacts by lazy {
        if (ingredients.isEmpty()) {
            NutritionFacts.Empty
        } else {
            ingredients.mapNotNull { it.nutritionFacts }.sum() / totalWeight * 100.0
        }
    }

    /**
     * Returns a list of all ingredients in the recipe, including those from nested recipes. If an
     * ingredient is a recipe, it recursively includes its ingredients. Each food is included only
     * once in the final list.
     */
    fun flatIngredients(): List<Food> =
        ingredients
            .flatMap { ingredient ->
                val food = ingredient.food
                if (food is Recipe) {
                    listOf(food) + food.flatIngredients()
                } else {
                    listOf(food)
                }
            }
            .distinct()

    /**
     * Unpacks the recipe into a list of ingredients with their weights adjusted according to the
     * specified measurement.
     *
     * @param weight The total weight to adjust the ingredients to.
     * @return A list of [RecipeIngredient] with weights adjusted according to the specified weight.
     */
    fun unpack(weight: Double): List<RecipeIngredient> {
        val fraction = weight / totalWeight

        return ingredients.map { (food, measurement) ->
            RecipeIngredient(food = food, measurement = measurement * fraction)
        }
    }
}
