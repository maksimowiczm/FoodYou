package com.maksimowiczm.foodyou.feature.recipe.domain

import com.maksimowiczm.foodyou.core.model.FoodId

internal fun interface UpdateRecipeUseCase {
    suspend operator fun invoke(
        recipeId: FoodId.Recipe,
        name: String,
        servings: Int,
        ingredients: List<Ingredient>
    )
}

// internal class UpdateRecipeUseCaseImpl(
//    private val recipeLocalDataSource: RecipeLocalDataSource,
//    private val ingredientMapper: IngredientMapper
// ) : UpdateRecipeUseCase {
//    override suspend fun invoke(
//        recipeId: FoodId.Recipe,
//        name: String,
//        servings: Int,
//        ingredients: List<Ingredient>
//    ) {
//        recipeLocalDataSource.updateRecipeWithIngredients(
//            recipeId = recipeId.id,
//            name = name,
//            servings = servings,
//            ingredients = ingredients.map(ingredientMapper::toEntity)
//        )
//    }
// }
