package com.maksimowiczm.foodyou.core.model

data class Recipe(
    override val id: FoodId.Recipe,
    val name: String,
    val servings: Int,
    val ingredients: List<RecipeIngredient>
) : Food {

    override val headline: String
        get() = name

    override val totalWeight: Float
        get() {
            val weight = ingredients.mapNotNull { it.weight }.fold(0f) { acc, ingredient ->
                acc + ingredient
            }
            return weight
        }

    override val servingWeight: Float
        get() = totalWeight / servings

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
                sum / totalWeight
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
