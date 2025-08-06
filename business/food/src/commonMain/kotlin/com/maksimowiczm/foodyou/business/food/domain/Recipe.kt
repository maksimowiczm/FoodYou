package com.maksimowiczm.foodyou.business.food.domain

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
