package com.maksimowiczm.foodyou.feature.recipe.domain

import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.domain.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.source.RecipeLocalDataSource

internal fun interface CreateRecipeUseCase {
    suspend operator fun invoke(
        name: String,
        servings: Int,
        ingredients: List<Ingredient>
    ): FoodId.Recipe
}

internal class CreateRecipeUseCaseImpl(
    private val recipeLocalDataSource: RecipeLocalDataSource,
    private val measurementMapper: MeasurementMapper = MeasurementMapper
) : CreateRecipeUseCase {
    override suspend fun invoke(
        name: String,
        servings: Int,
        ingredients: List<Ingredient>
    ): FoodId.Recipe {
        val id = recipeLocalDataSource.createRecipeWithIngredients(
            name = name,
            servings = servings,
            ingredients = ingredients.map { ingredient ->
                when (ingredient) {
                    is Ingredient.Product -> ingredient.toRecipeIngredientEntity()
                    is Ingredient.Recipe -> ingredient.toRecipeIngredientEntity()
                }
            }
        )

        return FoodId.Recipe(id)
    }

    private fun Ingredient.toRecipeIngredientEntity() = when (this) {
        is Ingredient.Product -> RecipeIngredientEntity(
            productId = food.id.id,
            recipeIngredientId = null,
            measurement = measurementMapper.toEntity(measurement),
            quantity = measurementMapper.toQuantity(measurement)
        )

        is Ingredient.Recipe -> RecipeIngredientEntity(
            productId = null,
            recipeIngredientId = food.id.id,
            measurement = measurementMapper.toEntity(measurement),
            quantity = measurementMapper.toQuantity(measurement)
        )
    }
}
