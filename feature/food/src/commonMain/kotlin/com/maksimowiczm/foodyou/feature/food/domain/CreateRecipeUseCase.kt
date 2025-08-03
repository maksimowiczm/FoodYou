package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.food.Recipe
import com.maksimowiczm.foodyou.feature.food.data.database.food.RecipeIngredient
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.rawValue
import com.maksimowiczm.foodyou.feature.measurement.domain.type

interface CreateRecipeUseCase {
    suspend fun create(
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<Pair<FoodId, Measurement>>,
        event: FoodEvent.FoodCreationEvent
    ): FoodId.Recipe
}

internal class CreateRecipeUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val foodEventMapper: FoodEventMapper
) : CreateRecipeUseCase {

    private val recipeDao = foodDatabase.recipeDao
    private val foodEventDao = foodDatabase.foodEventDao

    override suspend fun create(
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<Pair<FoodId, Measurement>>,
        event: FoodEvent.FoodCreationEvent
    ): FoodId.Recipe {
        val recipe = Recipe(
            name = name,
            servings = servings,
            note = note,
            isLiquid = isLiquid
        )

        val ingredients = ingredients.map { (foodId, measurement) ->
            RecipeIngredient(
                ingredientRecipeId = (foodId as? FoodId.Recipe)?.id,
                ingredientProductId = (foodId as? FoodId.Product)?.id,
                measurement = measurement.type,
                quantity = measurement.rawValue
            )
        }

        val id = recipeDao.insertRecipeWithIngredients(
            recipe = recipe,
            ingredients = ingredients
        ).let {
            FoodId.Recipe(it)
        }

        val eventEntity = foodEventMapper.toEntity(event, id)
        foodEventDao.insert(eventEntity)

        return id
    }
}
