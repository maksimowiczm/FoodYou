package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodId
import com.maksimowiczm.foodyou.business.food.domain.Measurement
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalRecipeDataSource
import com.maksimowiczm.foodyou.business.shared.domain.ErrorLoggingUtils
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDateTime

data class UpdateRecipeCommand(
    val id: FoodId.Recipe,
    val name: String,
    val servings: Int,
    val note: String?,
    val isLiquid: Boolean,
    val ingredients: List<Pair<FoodId, Measurement>>,
) : Command

sealed interface UpdateRecipeError {
    data class RecipeNotFound(val id: FoodId.Recipe) : UpdateRecipeError

    data class IngredientNotFound(val foodId: FoodId) : UpdateRecipeError

    data object CircularIngredient : UpdateRecipeError
}

internal class UpdateRecipeCommandHandler(
    private val recipeDataSource: LocalRecipeDataSource,
    private val productDataSource: LocalProductDataSource,
    private val eventDataSource: LocalFoodEventDataSource,
) : CommandHandler<UpdateRecipeCommand, Unit, UpdateRecipeError> {
    override val commandType = UpdateRecipeCommand::class

    override suspend fun handle(command: UpdateRecipeCommand): Result<Unit, UpdateRecipeError> {
        if (command.ingredients.any { (foodId, _) -> foodId == command.id }) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.CircularIngredient,
                message = { "Recipe cannot contain itself as an ingredient." },
            )
        }

        val recipe = recipeDataSource.observeRecipe(command.id).firstOrNull()

        if (recipe == null) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.RecipeNotFound(command.id),
                message = { "Recipe with ID ${command.id} not found." },
            )
        }

        val ingredients =
            command.ingredients.map { (foodId, measurement) ->
                val food =
                    when (foodId) {
                        is FoodId.Product -> productDataSource.observeProduct(foodId).firstOrNull()
                        is FoodId.Recipe -> recipeDataSource.observeRecipe(foodId).firstOrNull()
                    }

                if (food == null) {
                    return ErrorLoggingUtils.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = UpdateRecipeError.IngredientNotFound(foodId),
                        message = { "Ingredient with ID $foodId not found." },
                    )
                }

                RecipeIngredient(food, measurement)
            }

        val updatedRecipe =
            recipe.copy(
                name = command.name,
                servings = command.servings,
                note = command.note,
                isLiquid = command.isLiquid,
                ingredients = ingredients,
            )

        recipeDataSource.updateRecipe(updatedRecipe)

        eventDataSource.insert(command.id, FoodEvent.Edited(LocalDateTime.now()))

        return Ok(Unit)
    }

    private companion object {
        const val TAG = "UpdateRecipeCommandHandler"
    }
}
