package com.maksimowiczm.foodyou.feature.recipe.domain

import com.maksimowiczm.foodyou.core.database.food.RecipeLocalDataSource
import com.maksimowiczm.foodyou.core.model.FoodId

internal fun interface UpdateRecipeUseCase {
    suspend operator fun invoke(
        recipeId: FoodId.Recipe,
        name: String,
        servings: Int,
        isLiquid: Boolean,
        ingredients: List<Ingredient>,
        note: String
    )
}

internal class UpdateRecipeUseCaseImpl(
    private val recipeLocalDataSource: RecipeLocalDataSource,
    private val ingredientMapper: IngredientMapper
) : UpdateRecipeUseCase {
    override suspend fun invoke(
        recipeId: FoodId.Recipe,
        name: String,
        servings: Int,
        isLiquid: Boolean,
        ingredients: List<Ingredient>,
        note: String
    ) {
        recipeLocalDataSource.updateRecipeWithIngredients(
            recipeId = recipeId.id,
            name = name,
            servings = servings,
            ingredients = ingredients.map(ingredientMapper::toEntity),
            isLiquid = isLiquid,
            note = note
        )
    }
}
