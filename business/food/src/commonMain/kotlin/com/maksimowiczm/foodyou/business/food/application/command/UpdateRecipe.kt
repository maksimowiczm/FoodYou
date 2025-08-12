package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalRecipeDataSource
import com.maksimowiczm.foodyou.business.shared.domain.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.business.shared.domain.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
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
) : Command<Unit, UpdateRecipeError> {}

sealed interface UpdateRecipeError {
    data object EmptyName : UpdateRecipeError

    data object NonPositiveServings : UpdateRecipeError

    data object EmptyIngredients : UpdateRecipeError

    data class RecipeNotFound(val id: FoodId.Recipe) : UpdateRecipeError

    data class IngredientNotFound(val foodId: FoodId) : UpdateRecipeError

    data object CircularIngredient : UpdateRecipeError
}

internal class UpdateRecipeCommandHandler(
    private val recipeDataSource: LocalRecipeDataSource,
    private val productDataSource: LocalProductDataSource,
    private val eventDataSource: LocalFoodEventDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : CommandHandler<UpdateRecipeCommand, Unit, UpdateRecipeError> {

    override suspend fun handle(command: UpdateRecipeCommand): Result<Unit, UpdateRecipeError> {
        if (command.name.isBlank()) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.EmptyName,
                message = { "Recipe name cannot be empty." },
            )
        }

        if (command.servings <= 0) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.NonPositiveServings,
                message = { "Recipe servings must be a positive integer." },
            )
        }

        if (command.ingredients.isEmpty()) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.EmptyIngredients,
                message = { "Recipe must have at least one ingredient." },
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

        // Check for circular references in ingredients
        val flatIngredients = recipe.flatIngredients()
        val ingredientIds = flatIngredients.map { it.id }.toSet()
        if (flatIngredients.size != ingredientIds.size) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.CircularIngredient,
                message = { "Recipe contains circular ingredient references." },
            )
        }

        transactionProvider.withTransaction {
            recipeDataSource.updateRecipe(updatedRecipe)
            eventDataSource.insert(command.id, FoodEvent.Edited(LocalDateTime.now()))
        }

        return Ok(Unit)
    }

    private companion object {
        const val TAG = "UpdateRecipeCommandHandler"
    }
}
