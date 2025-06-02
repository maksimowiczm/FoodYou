package com.maksimowiczm.foodyou.feature.recipe.domain

import com.maksimowiczm.foodyou.core.model.FoodId

internal fun interface CreateRecipeUseCase {
    suspend operator fun invoke(
        name: String,
        servings: Int,
        ingredients: List<Ingredient>
    ): FoodId.Recipe
}

// internal class CreateRecipeUseCaseImpl(
//    private val recipeLocalDataSource: RecipeLocalDataSource,
//    private val ingredientMapper: IngredientMapper
// ) : CreateRecipeUseCase {
//    override suspend fun invoke(
//        name: String,
//        servings: Int,
//        ingredients: List<Ingredient>
//    ): FoodId.Recipe {
//        val id = recipeLocalDataSource.createRecipeWithIngredients(
//            name = name,
//            servings = servings,
//            ingredients = ingredients.map(ingredientMapper::toEntity)
//        )
//
//        return FoodId.Recipe(id)
//    }
// }
