package com.maksimowiczm.foodyou.feature.recipe.domain

import com.maksimowiczm.foodyou.core.database.food.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.domain.MeasurementMapper

internal interface IngredientMapper {
    fun toEntity(ingredient: Ingredient): RecipeIngredientEntity
}

internal class IngredientMapperImpl(private val measurementMapper: MeasurementMapper) :
    IngredientMapper {
    override fun toEntity(ingredient: Ingredient) = when (ingredient) {
        is Ingredient.Product -> RecipeIngredientEntity(
            ingredientProductId = ingredient.food.id.id,
            ingredientRecipeId = null,
            measurement = measurementMapper.toEntity(ingredient.measurement),
            quantity = measurementMapper.toQuantity(ingredient.measurement)
        )

        is Ingredient.Recipe -> RecipeIngredientEntity(
            ingredientProductId = null,
            ingredientRecipeId = ingredient.food.id.id,
            measurement = measurementMapper.toEntity(ingredient.measurement),
            quantity = measurementMapper.toQuantity(ingredient.measurement)
        )
    }
}
