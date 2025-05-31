package com.maksimowiczm.foodyou.feature.recipe.domain

import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.domain.mapper.MeasurementMapper

internal class IngredientMapper(
    private val measurementMapper: MeasurementMapper = MeasurementMapper
) {
    fun toEntity(ingredient: Ingredient) = when (ingredient) {
        is Ingredient.Product -> RecipeIngredientEntity(
            productId = ingredient.food.id.id,
            recipeIngredientId = null,
            measurement = measurementMapper.toEntity(ingredient.measurement),
            quantity = measurementMapper.toQuantity(ingredient.measurement)
        )

        is Ingredient.Recipe -> RecipeIngredientEntity(
            productId = null,
            recipeIngredientId = ingredient.food.id.id,
            measurement = measurementMapper.toEntity(ingredient.measurement),
            quantity = measurementMapper.toQuantity(ingredient.measurement)
        )
    }
}
