package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.food.RecipeIngredient
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.rawValue
import com.maksimowiczm.foodyou.feature.measurement.domain.type
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDateTime

interface UpdateRecipeUseCase {
    suspend fun update(
        id: FoodId.Recipe,
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<Pair<FoodId, Measurement>>
    )
}

internal class UpdateRecipeUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val foodEventMapper: FoodEventMapper,
    private val observeRecipeUseCase: ObserveRecipeUseCase
) : UpdateRecipeUseCase {

    private val recipeDao = foodDatabase.recipeDao
    private val foodEventDao = foodDatabase.foodEventDao

    override suspend fun update(
        id: FoodId.Recipe,
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<Pair<FoodId, Measurement>>
    ) {
        if (ingredients.any { (foodId, _) -> foodId == id }) {
            error("Recipe cannot contain itself as an ingredient.")
        }

        val oldDomainRecipe = observeRecipeUseCase.observe(id).firstOrNull()
        val oldRecipe = recipeDao.observe(id.id).firstOrNull()

        if (oldRecipe == null || oldDomainRecipe == null) {
            error("Recipe with id $id does not exist.")
        }

        val updatedRecipe = oldRecipe.copy(
            name = name,
            servings = servings,
            note = note,
            isLiquid = isLiquid
        )

        val updatedIngredients = ingredients.map { (foodId, measurement) ->
            RecipeIngredient(
                ingredientRecipeId = (foodId as? FoodId.Recipe)?.id,
                ingredientProductId = (foodId as? FoodId.Product)?.id,
                measurement = measurement.type,
                quantity = measurement.rawValue
            )
        }

        recipeDao.updateRecipeWithIngredients(
            recipe = updatedRecipe,
            ingredients = updatedIngredients
        )

        val eventEntity = foodEventMapper.toEntity(
            model = FoodEvent.Edited(
                date = LocalDateTime.now(),
                oldFood = oldDomainRecipe
            ),
            foodId = id
        )

        foodEventDao.insert(eventEntity)
    }
}
