package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.shared.domain.food.WeightedStrict
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId

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
) : FoodModel, WeightedStrict {
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
}
