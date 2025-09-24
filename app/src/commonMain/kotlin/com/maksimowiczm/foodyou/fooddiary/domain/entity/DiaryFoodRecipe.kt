package com.maksimowiczm.foodyou.fooddiary.domain.entity

import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.WeightCalculator
import com.maksimowiczm.foodyou.common.domain.food.sum
import com.maksimowiczm.foodyou.common.domain.measurement.Measurement

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
    override val note: String?,
) : DiaryFood {

    override val totalWeight: Double = ingredients.sumOf { it.weight }

    override val servingWeight: Double = totalWeight / servings

    /** The nutrition facts of the food item per 100g or 100ml. */
    override val nutritionFacts: NutritionFacts =
        ingredients.map { it.nutritionFacts }.sum() / totalWeight * 100.0

    override fun weight(measurement: Measurement) =
        WeightCalculator.calculateWeight(
            measurement = measurement,
            servingWeight = servingWeight,
            totalWeight = totalWeight,
        )

    /**
     * Returns a list of all ingredients in the recipe, including those from nested recipes. If an
     * ingredient is a recipe, it recursively includes its ingredients. Each food is included only
     * once in the final list.
     */
    fun flatIngredients(): List<DiaryFood> =
        ingredients
            .flatMap { ingredient ->
                val food = ingredient.food
                if (food is DiaryFoodRecipe) {
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
     * @param measurement The measurement to adjust the weights of the ingredients.
     * @return A list of [DiaryFoodRecipeIngredient] with weights adjusted according to the
     *   specified measurement.
     */
    fun unpack(measurement: Measurement): List<DiaryFoodRecipeIngredient> =
        unpack(weight(measurement))

    /**
     * Unpacks the recipe into a list of ingredients with their weights adjusted according to the
     * specified weight.
     *
     * @param weight The total weight to adjust the ingredients to.
     * @return A list of [DiaryFoodRecipeIngredient] with weights adjusted according to the
     *   specified weight.
     */
    fun unpack(weight: Double): List<DiaryFoodRecipeIngredient> {
        val fraction = weight / totalWeight

        return ingredients.map { ingredient ->
            ingredient.copy(measurement = ingredient.measurement * fraction)
        }
    }
}
