package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.sum
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

data class Recipe(
    override val id: FoodId.Recipe,
    val name: String,
    val servings: Int,
    val ingredients: List<RecipeIngredient>,
    val note: String?,
    override val isLiquid: Boolean,
) : Food {
    override val headline: String = name

    override val totalWeight = ingredients.mapNotNull { it.weight }.sum()

    override val servingWeight = totalWeight / servings

    override val nutritionFacts: NutritionFacts by lazy {
        if (ingredients.isEmpty() || totalWeight == 0.0) {
            NutritionFacts.Empty
        } else {
            ingredients.mapNotNull { it.nutritionFacts }.sum() / totalWeight
        }
    }

    /** Returns a flat list of all ingredients in the recipe. */
    fun allIngredients(): List<Food> =
        ingredients
            .flatMap { ingredient ->
                val food = ingredient.food
                if (food is Recipe) {
                    listOf(food) + food.allIngredients()
                } else {
                    listOf(food)
                }
            }
            .distinct()

    /** Calculates measurements for each ingredient based on the given weight. */
    fun measuredIngredients(weight: Double): List<RecipeIngredient> {
        val fraction = weight / totalWeight

        return ingredients.map { (food, measurement) ->
            val newMeasurement =
                when (measurement) {
                    is Measurement.Gram -> Measurement.Gram(measurement.value * fraction)

                    is Measurement.Milliliter ->
                        Measurement.Milliliter(measurement.value * fraction)

                    is Measurement.Package -> Measurement.Package(measurement.quantity * fraction)

                    is Measurement.Serving -> Measurement.Serving(measurement.quantity * fraction)
                }

            RecipeIngredient(food = food, measurement = newMeasurement)
        }
    }
}
