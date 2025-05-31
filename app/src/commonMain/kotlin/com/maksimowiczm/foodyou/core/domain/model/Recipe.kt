package com.maksimowiczm.foodyou.core.domain.model

import com.maksimowiczm.foodyou.core.ext.sumOf

data class Recipe(
    override val id: FoodId.Recipe,
    override val name: String,
    val servings: Int,
    val ingredients: List<RecipeIngredient>
) : Food {
    override val brand = null

    override val packageWeight: PortionWeight.Package
        get() {
            val weight = ingredients.mapNotNull { it.weight }.sumOf { it }
            return PortionWeight.Package(weight)
        }

    override val servingWeight: PortionWeight.Serving
        get() {
            val weight = ingredients.mapNotNull { it.weight }.sumOf { it }
            return PortionWeight.Serving(weight / servings)
        }

    override val nutritionFacts: NutritionFacts
        get() = if (ingredients.isEmpty()) {
            NutritionFacts.Empty
        } else {
            val sum = ingredients
                .map { it.food.nutritionFacts * (it.weight ?: 0f) }
                .sum()

            if (sum.isEmpty) {
                NutritionFacts.Empty
            } else {
                sum / packageWeight.weight
            }
        }

    /**
     * Returns a flat list of all ingredients in the recipe.
     */
    fun flatIngredients(): List<Food> = ingredients.flatMap { ingredient ->
        val food = ingredient.food
        if (food is Recipe) {
            listOf(food) + food.flatIngredients()
        } else {
            listOf(food)
        }
    }.distinct()
}
