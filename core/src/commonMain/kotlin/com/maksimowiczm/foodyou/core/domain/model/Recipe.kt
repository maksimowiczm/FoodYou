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
            ingredients
                .map { it.product.nutritionFacts * (it.weight ?: 0f) }
                .sum() / packageWeight.weight
        }
}
