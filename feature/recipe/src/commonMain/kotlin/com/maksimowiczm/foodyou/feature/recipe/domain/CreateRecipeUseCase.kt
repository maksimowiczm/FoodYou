package com.maksimowiczm.foodyou.feature.recipe.domain

import com.maksimowiczm.foodyou.core.database.food.RecipeLocalDataSource
import com.maksimowiczm.foodyou.core.model.FoodId

internal fun interface CreateRecipeUseCase {
    suspend operator fun invoke(
        name: String,
        servings: Int,
        isLiquid: Boolean,
        ingredients: List<Ingredient>,
        note: String?
    ): FoodId.Recipe
}

internal class CreateRecipeUseCaseImpl(
    private val recipeLocalDataSource: RecipeLocalDataSource,
    private val ingredientMapper: IngredientMapper
) : CreateRecipeUseCase {
    override suspend fun invoke(
        name: String,
        servings: Int,
        isLiquid: Boolean,
        ingredients: List<Ingredient>,
        note: String?
    ): FoodId.Recipe {
        val id = recipeLocalDataSource.createRecipeWithIngredients(
            name = name,
            servings = servings,
            ingredients = ingredients.map(ingredientMapper::toEntity),
            isLiquid = isLiquid,
            note = note?.trim()?.takeIf { it.isNotEmpty() }
        )

        return FoodId.Recipe(id)
    }
}
