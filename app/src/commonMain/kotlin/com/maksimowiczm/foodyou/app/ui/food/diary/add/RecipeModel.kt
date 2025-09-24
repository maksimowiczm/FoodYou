package com.maksimowiczm.foodyou.app.ui.food.diary.add

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.WeightCalculator
import com.maksimowiczm.foodyou.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.entity.Recipe

@Immutable
internal data class RecipeModel(
    override val foodId: FoodId.Recipe,
    override val name: String,
    override val nutritionFacts: NutritionFacts,
    override val isLiquid: Boolean,
    override val note: String?,
    override val totalWeight: Double,
    val servings: Int,
    private val ingredients: List<IngredientModel>,
    val allIngredients: List<Triple<FoodId, String, NutritionFacts>>,
) : FoodModel {
    override val servingWeight: Double = totalWeight / servings

    constructor(
        recipe: Recipe
    ) : this(
        foodId = recipe.id,
        name = recipe.headline,
        nutritionFacts = recipe.nutritionFacts,
        isLiquid = recipe.isLiquid,
        note = recipe.note,
        totalWeight = recipe.totalWeight,
        servings = recipe.servings,
        ingredients = recipe.ingredients.map(::IngredientModel).sortedBy { it.name.lowercase() },
        allIngredients =
            recipe.flatIngredients().map { Triple(it.id, it.headline, it.nutritionFacts) },
    )

    fun unpack(weight: Double): List<IngredientModel> {
        val fraction = weight / totalWeight

        return ingredients.map { ingredient ->
            ingredient.copy(
                measurement = ingredient.measurement * fraction,
                nutritionFacts = ingredient.nutritionFacts?.times(fraction),
            )
        }
    }

    override fun weight(measurement: Measurement): Double =
        WeightCalculator.calculateWeight(
            measurement = measurement,
            servingWeight = servingWeight,
            totalWeight = totalWeight,
        )
}
