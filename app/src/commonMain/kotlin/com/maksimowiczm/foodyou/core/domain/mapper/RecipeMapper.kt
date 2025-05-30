package com.maksimowiczm.foodyou.core.domain.mapper

import com.maksimowiczm.foodyou.core.data.model.measurement.RecipeWithMeasurement
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeWithIngredients
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Recipe
import com.maksimowiczm.foodyou.core.domain.model.RecipeIngredient

object RecipeMapper {
    @JvmName("toRecipeModel")
    fun toModel(recipeWithIngredients: RecipeWithIngredients): Recipe =
        recipeWithIngredients.toModel()

    fun RecipeWithIngredients.toModel(): Recipe {
        val (recipeEntity, ingredients) = this

        val products = ingredients.map { ingredient ->

            val product = with(ProductMapper) { ingredient.product.toModel() }
            val measurement =
                with(MeasurementMapper) { ingredient.recipeIngredientEntity.toMeasurement() }

            RecipeIngredient(
                food = product,
                measurement = measurement
            )
        }

        return Recipe(
            id = FoodId.Recipe(recipeEntity.id),
            name = recipeEntity.name,
            servings = recipeEntity.servings,
            ingredients = products
        )
    }

    fun RecipeWithMeasurement.toModel(): Recipe {
        val ingredients = ingredients.map {
            val product = with(ProductMapper) { it.product.toModel() }
            val measurement = with(MeasurementMapper) { it.recipeIngredientEntity.toMeasurement() }

            RecipeIngredient(
                food = product,
                measurement = measurement
            )
        }

        return Recipe(
            id = FoodId.Recipe(recipe.id),
            name = recipe.name,
            servings = recipe.servings,
            ingredients = ingredients
        )
    }
}
